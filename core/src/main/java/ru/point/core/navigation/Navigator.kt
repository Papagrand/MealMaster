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
    fun fromHomeProgressFragmentToFastingFragment()
    fun fromHomeProgressFragmentToRecipesFragment()
    fun fromHomeProgressFragmentToProfileFragment()
    fun fromRecipesFragmentToHomeProgressFragment()
    fun fromRecipesFragmentToFastingFragment()
    fun fromRecipesFragmentToProfileFragment()
    fun fromFastingFragmentToHomeProgressFragment()
    fun fromFastingFragmentToProfileFragment()
    fun fromFastingFragmentToRecipesFragment()
    fun fromProfileFragmentToHomeProgressFragment()
    fun fromProfileFragmentToRecipesFragment()
    fun fromProfileFragmentToFastingFragment()

    //Временно
    fun fromHomeProgressFragmentToMealProductSearchFragment()
    fun fromHomeProgressFragmentToSettingSearchedProductFragment()
}