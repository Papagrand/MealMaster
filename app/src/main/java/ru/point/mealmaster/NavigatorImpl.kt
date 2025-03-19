package ru.point.mealmaster

import androidx.navigation.NavController
import ru.point.core.navigation.Navigator

class NavigatorImpl(private val navController: NavController): Navigator {
    override fun popBackStack() {
        navController.popBackStack()
    }

    override fun fromLoginFragmentToRegisterFragment() {
        navController.navigateSafe(R.id.action_loginFragment_to_registerFragment)
    }

    override fun fromRegisterFragmentToLoginFragment() {
        navController.navigateSafe(R.id.action_registerFragment_to_loginFragment)
    }

    override fun fromLoginFragmentToHomeProgressFragment(){
        navController.navigateSafe(R.id.action_loginFragment_to_homeProgressFragment)
    }

    override fun fromSplashFragmentToLoginFragment() {
        navController.navigateSafe(R.id.action_splashFragment_to_loginFragment)
    }

    override fun fromSplashFragmentToOnboardingFragment() {
        navController.navigateSafe(R.id.action_splashFragment_to_onboardingFragment)
    }

    override fun fromSplashFragmentToHomeProgressFragment() {
        navController.navigateSafe(R.id.action_splashFragment_to_homeProgressFragment)
    }

    override fun fromLoginFragmentToOnboardingFragment() {
        navController.navigateSafe(R.id.action_loginFragment_to_onboardingFragment)
    }

    override fun fromCreateProfileFirstStepFragmentToCreateProfileSecondStepFragment() {
        navController.navigateSafe(R.id.action_createProfileFirstStepFragment_to_createProfileSecondStepFragment)
    }

    override fun fromCreateProfileSecondStepFragmentToCreateProfileThirdStepFragment() {
        navController.navigateSafe(R.id.action_createProfileSecondStepFragment_to_createProfileThirdStepFragment)
    }

    override fun fromCreateProfileThirdStepFragmentToCreateProfileFourthStepFragment() {
        navController.navigateSafe(R.id.action_createProfileThirdStepFragment_to_createProfileFourthStepFragment)
    }


}
