package ru.point.auth.ui.on_boarding.domain

import ru.point.api.profile_creating.data.OnboardingProfileData
import ru.point.api.profile_creating.domain.ProfileCreateResult
import ru.point.api.profile_creating.domain.ProfileRepository

class CreateProfileUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        userProfileId: String,
        profileData: OnboardingProfileData
    ): ProfileCreateResult {
        return profileRepository.createProfile(userProfileId, profileData)
    }
}