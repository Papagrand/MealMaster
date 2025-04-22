package ru.point.core.navigation

import android.os.Bundle

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
    fun fromProfileFragmentToUpdateProfileInformationFragment(bundle: Bundle)
    fun fromUpdateProfileInformationFragmentToProfileFragment()
    fun fromMealFragmentToHomeProgressFragment()
    fun fromHomeProgressFragmentToMealProductSearchFragment(bundle: Bundle)
    fun fromMealFragmentToSearchedProductFragment(bundle: Bundle)
    fun fromSearchedProductFragmentToMealFragment(bundle: Bundle)
    //Временно
    fun fromHomeProgressFragmentToRecipeInformationFragment()
}