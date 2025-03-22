package ru.point.auth.ui.on_boarding

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.point.auth.R
import ru.point.auth.databinding.FragmentOnboardingBinding
import ru.point.auth.ui.profile_create.first_step.CreateProfileFirstStepFragment
import ru.point.auth.ui.profile_create.fourth_step.CreateProfileFourthStepFragment
import ru.point.auth.ui.profile_create.second_step.CreateProfileSecondStepFragment
import ru.point.auth.ui.profile_create.third_step.CreateProfileThirdStepFragment
import ru.point.core.navigation.BottomBarManager
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.ui.BaseFragment
import javax.inject.Inject

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {

    @Inject
    lateinit var onboardindViewModelFactory: OnboardingViewModel.Factory

    private val onboardingViewModel: OnboardingViewModel by activityViewModels {
        onboardindViewModelFactory
    }


    override fun onAttach(context: Context) {
        DaggerOnboardingComponent.builder()
            .deps(OnboardingDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
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

        val viewPagerAdapter = ViewPagerAdapter(requireActivity(), list)

        binding.viewPagerCreateProfile.isUserInputEnabled = false

        binding.viewPagerCreateProfile.adapter = viewPagerAdapter

        binding.goToNextStep.setOnClickListener {
            val currentFragment = list[currentPosition]
            if (currentFragment is OnboardingStep) {
                currentFragment.saveData() // saveData() обновляет canGoToNextStep во ViewModel
            }
            if (onboardingViewModel.getCanGo()) {
                binding.viewPagerCreateProfile.setCurrentItem(currentPosition + 1, true)
                // После перехода можно сбросить флаг, чтобы требовать повторной валидации при возвращении
                onboardingViewModel.updateCanGo(false)
            } else {
                // Если данные некорректны, можно показать уведомление пользователю
                when (currentPosition) {
                    0 -> Snackbar.make(
                        binding.root,
                        "Неверно заполнены данные. Проверьте введённую информацию.",
                        Snackbar.LENGTH_LONG
                    ).show()

                    1 -> Snackbar.make(
                        binding.root,
                        "Вы не выбрали уровень физической активности",
                        Snackbar.LENGTH_LONG
                    ).show()

                    2 -> Snackbar.make(binding.root, "Вы не выбрали цель", Snackbar.LENGTH_LONG)
                        .show()

                    3 -> Snackbar.make(
                        binding.root,
                        "Неверно заполнены данные. Проверьте введённую информацию.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }



        binding.goToPreviousStep.setOnClickListener {
            binding.viewPagerCreateProfile.setCurrentItem(currentPosition - 1, true)
        }

        binding.createProfileButton.setOnClickListener {
            val currentFragment = list[currentPosition]
            if (currentFragment is OnboardingStep) {
                currentFragment.saveData()
            }
            val lossMessage = getString(R.string.dialog_loss_text)
            val gainMessage = getString(R.string.dialog_gain_text)
            lifecycleScope.launch {
                if (onboardingViewModel.getCanGo() && onboardingViewModel.checkMassRate(lossMessage, gainMessage)) {
                    val userProfileId = SecurePrefs.getUserId()
                    if (userProfileId != null) {
                        onboardingViewModel.createProfile(userProfileId)
                    }
                }
            }
        }

        binding.viewPagerCreateProfile.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.goToPreviousStep.isVisible = position != 0
                binding.goToNextStep.isVisible = position != list.lastIndex
                binding.createProfileButton.isVisible = position == list.lastIndex
                currentPosition = position
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                onboardingViewModel.uiEvent.collect { event ->
                    when (event) {
                        is OnboardingUiEvent.NavigateToHomeProgress -> {
                            navigator.fromOnboardingFragmentToHomeProgressFragment()
                        }

                        is OnboardingUiEvent.ShowSnackBar -> {
                            Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                        }

                        is OnboardingUiEvent.ShowWarningDialog -> {
                            showWarningDialog(event.dialogData)
                        }
                    }
                }
            }
        }

    }

    private fun showWarningDialog(dialogData: DialogData) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(dialogData.message)
            .setTitle("Предупреждение пользователя")
            .setPositiveButton(dialogData.positiveButtonText) { dialog, _ ->
                // Если пользователь выбрал "Все равно завершить" — продолжаем создание профиля
                val userProfileId = SecurePrefs.getUserId()
                if (userProfileId != null) {
                    onboardingViewModel.createProfile(userProfileId)
                }
                dialog.dismiss()
            }
            .setNegativeButton(dialogData.negativeButtonText) { dialog, _ ->
                // Если пользователь отменил — просто закрываем диалог
                dialog.dismiss()
            }
            .show()
    }

}


class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val list: List<Fragment>) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment = list[position]

}