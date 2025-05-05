package ru.point.auth.ui.on_boarding.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.point.auth.R
import ru.point.auth.databinding.FragmentOnboardingBinding
import ru.point.auth.ui.on_boarding.di.DaggerOnboardingComponent
import ru.point.auth.ui.on_boarding.di.OnboardingDepsProvider
import ru.point.auth.ui.profile_create.first_step.CreateProfileFirstStepFragment
import ru.point.auth.ui.profile_create.fourth_step.CreateProfileFourthStepFragment
import ru.point.auth.ui.profile_create.second_step.CreateProfileSecondStepFragment
import ru.point.auth.ui.profile_create.third_step.CreateProfileThirdStepFragment
import ru.point.core.ContentLoadListener
import ru.point.core.navigation.BottomBarManager
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.ui.BaseFragment
import javax.inject.Inject

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {

    @Inject
    lateinit var onboardindViewModelFactory: OnboardingViewModelFactory

    private val onboardingViewModel: OnboardingViewModel by activityViewModels {
        onboardindViewModelFactory
    }

    private var listener: ContentLoadListener? = null

    override fun onAttach(context: Context) {
        DaggerOnboardingComponent.builder()
            .deps(OnboardingDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)

        listener = context as? ContentLoadListener
            ?: throw IllegalStateException(
                "Host must implement ContentLoadListener"
            )
    }


    private var currentPosition = 0

    private val list: List<Fragment> = listOf(
        CreateProfileFirstStepFragment.getNewInstance(),
        CreateProfileSecondStepFragment.getNewInstance(),
        CreateProfileThirdStepFragment.getNewInstance(),
        CreateProfileFourthStepFragment.getNewInstance(),
    )

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentOnboardingBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        lifecycleScope.launch {
            listener?.onContentLoaded()
        }

        val viewPagerAdapter = ViewPagerAdapter(requireActivity(), list)
        binding.viewPagerCreateProfile.isUserInputEnabled = false
        binding.viewPagerCreateProfile.adapter = viewPagerAdapter

        // Обработка кнопки "Далее"
        binding.goToNextStep.setOnClickListener {
            val currentFragment = list[currentPosition]
            if (currentFragment is OnboardingStep) {
                // Сохраняем данные во ViewModel
                currentFragment.saveData()
            }
            // Проверяем, можно ли переходить дальше
            if (onboardingViewModel.getCanGo()) {
                binding.viewPagerCreateProfile.setCurrentItem(currentPosition + 1, true)
                // Сбрасываем canGo, чтобы заново проверять на следующем шаге
                onboardingViewModel.updateCanGo(false)
            } else {
                // Показать ошибку пользователю
                showFillDataError(currentPosition)
            }
        }

        // Обработка кнопки "Назад"
        binding.goToPreviousStep.setOnClickListener {
            binding.viewPagerCreateProfile.setCurrentItem(currentPosition - 1, true)
        }

        // Обработка кнопки "Создать профиль" (на последнем шаге)
        binding.createProfileButton.setOnClickListener {
            val currentFragment = list[currentPosition]
            if (currentFragment is OnboardingStep) {
                currentFragment.saveData()
            }
            val lossMessage = getString(R.string.dialog_loss_text)
            val gainMessage = getString(R.string.dialog_gain_text)
            lifecycleScope.launch {
                if (onboardingViewModel.getCanGo()
                    && onboardingViewModel.checkMassRate(lossMessage, gainMessage)
                ) {
                    val userProfileId = SecurePrefs.getUserId()
                    if (userProfileId != null) {
                        onboardingViewModel.createProfile(userProfileId)
                    }
                }
            }
        }

        // Следим за сменой страницы ViewPager
        binding.viewPagerCreateProfile.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.goToPreviousStep.isVisible = position != 0
                binding.goToNextStep.isVisible = position != list.lastIndex
                binding.createProfileButton.isVisible = position == list.lastIndex
                currentPosition = position
            }
        })

        // Подписываемся на события UI из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                onboardingViewModel.uiEvent.collect { event ->
                    when (event) {
                        is OnboardingUiEvent.NavigateToHomeProgress -> {
                            // Переход на основной экран
                            navigator.fromOnboardingFragmentToHomeProgressFragment()
                        }
                        is OnboardingUiEvent.ShowSnackBar -> {
                            Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                        }
                        is OnboardingUiEvent.ShowWarningDialog -> {
                            val dialogData = OnboardingUiEvent.ShowWarningDialog(
                                message = event.message,
                                positiveButtonText = event.positiveButtonText,
                                negativeButtonText = event.negativeButtonText
                            )
                            showWarningDialog(dialogData)
                        }
                    }
                }
            }
        }
    }

    private fun showWarningDialog(dialogData: OnboardingUiEvent.ShowWarningDialog) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(dialogData.message)
            .setTitle("Предупреждение пользователя")
            .setPositiveButton(dialogData.positiveButtonText) { dialog, _ ->
                val userProfileId = SecurePrefs.getUserId()
                if (userProfileId != null) {
                    onboardingViewModel.createProfile(userProfileId)
                }
                dialog.dismiss()
            }
            .setNegativeButton(dialogData.negativeButtonText) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showFillDataError(stepPosition: Int) {
        val message = when (stepPosition) {
            0 -> "Неверно заполнены данные. Проверьте введённую информацию."
            1 -> "Вы не выбрали уровень физической активности."
            2 -> "Вы не выбрали цель."
            3 -> "Неверно заполнены данные. Проверьте введённую информацию."
            else -> "Проверьте введённые данные."
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}


class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val list: List<Fragment>) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment = list[position]

}