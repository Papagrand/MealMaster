package ru.point.api.profile_data.data

import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.api.profile_data.domain.UpdateProfileResult
import ru.point.api.profile_data.domain.models.ProfileDataModel
import ru.point.api.profile_data.domain.models.ProfileMainDataModel
import ru.point.core.enums.ActivityLevel
import ru.point.core.enums.UserGoal

class ProfileDataRepositoryImpl(
    private val profileDataService: ProfileDataService
) : ProfileDataRepository {
    override suspend fun getProfileMainData(userProfileId: String): Result<ProfileDataModel<ProfileMainDataModel>> {
        return try {
            val response = profileDataService.getMainProfileData(userProfileId)
            val body = response.body()
            when (response.code()) {
                200 -> {
                    if (body != null && body.success && body.data != null) {
                        val domainModel =
                            ProfileMainDataModel(
                                userProfileId = body.data.userProfileId,
                                name = body.data.name,
                                secName = body.data.secName ?: "",
                                height = body.data.height,
                                weight = body.data.weight,
                                age = body.data.age,
                                gender = body.data.gender,
                                activityLevel = body.data.activityLevel,
                                goal = body.data.goal,
                                goalWeight = body.data.goalWeight,
                                goalTimeStart = body.data.goalTimeStart,
                                goalTimeEnd = body.data.goalTimeEnd,
                                profilePicture = body.data.profilePicture
                            )
                        Result.success(ProfileDataModel(success = true, data = domainModel))
                    } else {
                        Result.failure(Throwable(body?.message ?: "Unexpected fault"))
                    }
                }

                403 -> {
                    Result.failure(Throwable(body?.message ?: "Profile not found"))
                }

                else -> {
                    Result.failure(Throwable(body?.message ?: "Unexpected error"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfileInformation(
        userProfileId: String,
        newProfileData: UpdatedProfileData
    ): UpdateProfileResult {
        return try {
            val response = profileDataService.updateProfileInformation(
                UpdateProfileRequest(
                    userProfileId = userProfileId,
                    name = newProfileData.firstName,
                    secName = newProfileData.lastName,
                    age = newProfileData.age,
                    activityLevel = ActivityLevel.fromString(newProfileData.activityLevel)?.level ?: 3,
                    goal = UserGoal.fromString(newProfileData.goal)?.goalNumber ?: 1,
                    gender = newProfileData.gender,
                    goalWeight = newProfileData.targetWeight,
                    goalTimeEnd = newProfileData.dietEndDate
                )
            )
            if (response.success) {
                UpdateProfileResult.Success
            } else {
                UpdateProfileResult.Error(response.message ?: "Неизвестная ошибка")
            }
        } catch (e: Exception) {
            UpdateProfileResult.Error(e.message ?: "Ошибка соединения")
        }
    }
}
