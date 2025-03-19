package ru.point.auth.ui.on_boarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import ru.point.auth.databinding.FragmentOnboardingBinding
import ru.point.auth.ui.profile_create.first_step.CreateProfileFirstStepFragment
import ru.point.auth.ui.profile_create.fourth_step.CreateProfileFourthStepFragment
import ru.point.auth.ui.profile_create.second_step.CreateProfileSecondStepFragment
import ru.point.auth.ui.profile_create.third_step.CreateProfileThirdStepFragment
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

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
            if (onboardingViewModel.profileData.value.canGoToNextStep) {
                binding.viewPagerCreateProfile.setCurrentItem(currentPosition + 1, true)
                // После перехода можно сбросить флаг, чтобы требовать повторной валидации при возвращении
                onboardingViewModel.updateCanGo(false)
            } else {
                // Если данные некорректны, можно показать уведомление пользователю
                when(currentPosition){
                    0 -> Snackbar.make(binding.root, "Неверно заполнены данные. Проверьте введённую информацию.", Snackbar.LENGTH_LONG).show()
                    1 -> Snackbar.make(binding.root, "Вы не выбрали уровень физической активности", Snackbar.LENGTH_LONG).show()
                    2 -> Snackbar.make(binding.root, "Вы не выбрали цель", Snackbar.LENGTH_LONG).show()
                    3 -> Snackbar.make(binding.root, "Неверно заполнены данные. Проверьте введённую информацию.", Snackbar.LENGTH_LONG).show()
                }
            }
        }



        binding.goToPreviousStep.setOnClickListener {
            binding.viewPagerCreateProfile.setCurrentItem(currentPosition - 1, true)
        }

        binding.viewPagerCreateProfile.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.goToPreviousStep.isVisible = position != 0
                binding.goToNextStep.isVisible = position != list.lastIndex
                binding.createProfileButton.isVisible = position == list.lastIndex
                currentPosition = position

                val data = onboardingViewModel.profileData.value
                Log.i(
                    "DataAboba", "firstName: ${data.firstName}, lastName: ${data.lastName}, " +
                            "height: ${data.height}, weight: ${data.weight}, age: ${data.age}, " +
                            "gender: ${data.gender}, activityLevel: ${data.activityLevel}, " +
                            "goal: ${data.goal}, targetWeight: ${data.targetWeight}, dietEndDate: ${data.dietEndDate}"
                )
            }
        })
    }

}

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val list: List<Fragment>) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment = list[position]

}