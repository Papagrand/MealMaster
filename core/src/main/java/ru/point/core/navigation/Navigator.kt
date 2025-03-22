package ru.point.core.navigation

interface Navigator {
    fun popBackStack()
    fun fromLoginFragmentToRegisterFragment()
    fun fromRegisterFragmentToLoginFragment()
    fun fromLoginFragmentToOnboardingFragment()
    fun fromCreateProfileFirstStepFragmentToCreateProfileSecondStepFragment()
    fun fromCreateProfileSecondStepFragmentToCreateProfileThirdStepFragment()
    fun fromCreateProfileThirdStepFragmentToCreateProfileFourthStepFragment()
    fun fromLoginFragmentToHomeProgressFragment()
    fun fromSplashFragmentToLoginFragment()
    fun fromSplashFragmentToOnboardingFragment()
    fun fromSplashFragmentToHomeProgressFragment()
    fun fromOnboardingFragmentToHomeProgressFragment()
}