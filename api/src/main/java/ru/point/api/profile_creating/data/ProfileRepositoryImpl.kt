package ru.point.api.profile_creating.data

import ru.point.api.profile_creating.domain.ProfileCreateResult
import ru.point.api.profile_creating.domain.ProfileRepository


class ProfileRepositoryImpl(
    private val profileService: ProfileService
) : ProfileRepository {

    override suspend fun createProfile(
        userProfileId: String,
        profileData: OnboardingProfileData
    ): ProfileCreateResult {
        return try {
            val response = profileService.createProfile(
                CreateProfileRequest(
                    userProfileId = userProfileId,
                    name = profileData.firstName,
                    secName = profileData.lastName,
                    height = profileData.height.toDouble(),
                    weight = profileData.weight.toDouble(),
                    age = profileData.age,
                    gender = profileData.gender,
                    activityLevel = profileData.activityLevel,
                    goal = profileData.goal,
                    goalWeight = profileData.targetWeight.toDouble(),
                    goalTimeEnd = profileData.dietEndDate
                )
            )
            if (response.success) {
                ProfileCreateResult.Success
            } else {
                ProfileCreateResult.Error(response.message ?: "Неизвестная ошибка")
            }
        } catch (e: Exception) {
            ProfileCreateResult.Error(e.message ?: "Неизвестная ошибка")
        }
    }
}