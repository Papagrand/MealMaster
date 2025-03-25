package ru.point.mealmaster

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import ru.point.core.navigation.Navigator

class NavigatorImpl(private val navController: NavController): Navigator {
    override fun popBackStack() {
        navController.popBackStack()
    }

    override fun fromLoginFragmentToRegisterFragment() {
        navController.navigateSafe(R.id.action_loginFragment_to_registerFragment)
    }

    override fun fromRegisterFragmentToLoginFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.registerFragment, inclusive = true)
            .build()
        navController.navigate(R.id.action_registerFragment_to_loginFragment, null, navOptions)
    }

    override fun fromLoginFragmentToHomeProgressFragment(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, inclusive = true)
            .build()
        navController.navigate(R.id.action_loginFragment_to_homeProgressFragment, null, navOptions)
    }

    override fun fromSplashFragmentToLoginFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.splashFragment, inclusive = true)
            .build()
        navController.navigate(R.id.action_splashFragment_to_loginFragment, null, navOptions)
    }

    override fun fromSplashFragmentToOnboardingFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.splashFragment, inclusive = true)
            .build()
        navController.navigate(R.id.action_splashFragment_to_onboardingFragment, null, navOptions)
    }

    override fun fromSplashFragmentToHomeProgressFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.splashFragment, inclusive = true)
            .build()
        navController.navigate(R.id.action_splashFragment_to_homeProgressFragment, null, navOptions)
    }

    override fun fromOnboardingFragmentToHomeProgressFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.onboardingFragment, inclusive = true)
            .build()
        navController.navigate(R.id.action_onboardingFragment_to_homeProgressFragment, null, navOptions)
    }

    override fun fromHomeProgressFragmentToFastingFragment() {
        navController.navigateSafe(R.id.action_homeProgressFragment_to_fastingFragment)
    }

    override fun fromHomeProgressFragmentToRecipesFragment() {
        navController.navigateSafe(R.id.action_homeProgressFragment_to_recipesFragment)
    }

    override fun fromHomeProgressFragmentToProfileFragment() {
        navController.navigateSafe(R.id.action_homeProgressFragment_to_profileFragment)
    }

    override fun fromRecipesFragmentToHomeProgressFragment() {
        navController.navigateSafe(R.id.action_recipesFragment_to_homeProgressFragment)
    }

    override fun fromRecipesFragmentToFastingFragment() {
        navController.navigateSafe(R.id.action_recipesFragment_to_fastingFragment)
    }

    override fun fromRecipesFragmentToProfileFragment() {
        navController.navigateSafe(R.id.action_recipesFragment_to_profileFragment)
    }

    override fun fromFastingFragmentToHomeProgressFragment() {
        navController.navigateSafe(R.id.action_fastingFragment_to_homeProgressFragment)
    }

    override fun fromFastingFragmentToProfileFragment() {
        navController.navigateSafe(R.id.action_fastingFragment_to_profileFragment)
    }

    override fun fromFastingFragmentToRecipesFragment() {
        navController.navigateSafe(R.id.action_fastingFragment_to_recipesFragment)
    }

    override fun fromProfileFragmentToHomeProgressFragment() {
        navController.navigateSafe(R.id.action_profileFragment_to_homeProgressFragment)
    }

    override fun fromProfileFragmentToRecipesFragment() {
        navController.navigateSafe(R.id.action_profileFragment_to_recipesFragment)
    }

    override fun fromProfileFragmentToFastingFragment() {
        navController.navigateSafe(R.id.action_profileFragment_to_fastingFragment)
    }

    override fun fromLoginFragmentToOnboardingFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, inclusive = true)
            .build()
        navController.navigate(R.id.action_loginFragment_to_onboardingFragment, null, navOptions)
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
