package ru.point.api.profile_creating.domain

import ru.point.api.profile_creating.data.OnboardingProfileData

interface ProfileRepository {
    suspend fun createProfile(
        userProfileId: String,
        profileData: OnboardingProfileData
    ): ProfileCreateResult
}