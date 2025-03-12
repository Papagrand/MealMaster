package ru.point.core.navigation

interface Navigator {
    fun popBackStack()
    fun fromLoginFragmentToRegisterFragment()
    fun fromRegisterFragmentToLoginFragment()
    fun fromLoginFragmentToCreateProfileFirstStepFragment()
    fun fromCreateProfileFirstStepFragmentToCreateProfileSecondStepFragment()
    fun fromCreateProfileSecondStepFragmentToCreateProfileThirdStepFragment()
    fun fromCreateProfileThirdStepFragmentToCreateProfileFourthStepFragment()
}