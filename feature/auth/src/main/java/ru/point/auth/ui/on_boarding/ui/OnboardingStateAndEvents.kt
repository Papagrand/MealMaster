package ru.point.auth.ui.on_boarding.ui

import ru.point.api.profile_creating.data.OnboardingProfileData

data class OnboardingViewState(
    val profileData: OnboardingProfileData = OnboardingProfileData(),
    val canGoToNextStep: Boolean = false
)

sealed class OnboardingUiEvent {
    object NavigateToHomeProgress : OnboardingUiEvent()
    data class ShowSnackBar(val message: String) : OnboardingUiEvent()
    data class ShowWarningDialog(
        val message: String,
        val positiveButtonText: String,
        val negativeButtonText: String
    ) : OnboardingUiEvent()
}