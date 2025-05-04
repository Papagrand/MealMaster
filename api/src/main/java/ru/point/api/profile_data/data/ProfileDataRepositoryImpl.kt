package ru.point.api.profile_data.data

import ru.point.api.meal.data.UpdateDeleteMealItemResponse
import ru.point.api.meal.domain.UpdateMealItemResult
import ru.point.api.profile_data.domain.LogoutUserResult
import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.api.profile_data.domain.UpdateProfileResult
import ru.point.api.profile_data.domain.models.MainMaxNutrientsDataModel
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

    override suspend fun getMainMaxNutrientsData(userProfileId: String): Result<ProfileDataModel<MainMaxNutrientsDataModel>> {
        return try {
            val response = profileDataService.getMainNutrientsData(userProfileId)
            val body = response.body()
            when (response.code()) {
                200 -> {
                    if (body != null && body.success && body.data != null) {
                        val domainModel =
                            MainMaxNutrientsDataModel(
                                maxCalories = body.data.maxCalories,
                                maxBreakfastCalories = body.data.maxBreakfastCalories,
                                maxLunchCalories = body.data.maxLunchCalories,
                                maxDinnerCalories = body.data.maxDinnerCalories,
                                maxSnackCalories = body.data.maxSnackCalories,
                                maxProtein = body.data.maxProtein,
                                maxCarbohydrates = body.data.maxCarbohydrates,
                                maxFat = body.data.maxFat,
                                maxDietaryFiber = body.data.maxDietaryFiber,
                                maxSugars = body.data.maxSugars,
                                maxStarchDextrins = body.data.maxStarchDextrins,
                                maxPotassium = body.data.maxPotassium,
                                maxCalcium = body.data.maxCalcium,
                                maxSilicon = body.data.maxSilicon,
                                maxMagnesium = body.data.maxMagnesium,
                                maxSodium = body.data.maxSodium,
                                maxSulfur = body.data.maxSulfur,
                                maxPhosphorus = body.data.maxPhosphorus,
                                maxChlorine = body.data.maxChlorine,
                                maxIron = body.data.maxIron,
                                maxZinc = body.data.maxZinc,
                                maxOmega3 = body.data.maxOmega3,
                                maxOmega6 = body.data.maxOmega6,
                                maxVitaminA = body.data.maxVitaminA,
                                maxVitaminB1 = body.data.maxVitaminB1,
                                maxVitaminB2 = body.data.maxVitaminB2,
                                maxVitaminB4 = body.data.maxVitaminB4,
                                maxVitaminC = body.data.maxVitaminC,
                                maxVitaminD = body.data.maxVitaminD,
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

    override suspend fun logoutUser(userId: String, deviceId: String): LogoutUserResult {
        return try {
            val response: LogoutUserResponse = profileDataService.logoutFromProfile(userId, deviceId)
            if (response.success){
                LogoutUserResult.Success
            }else{
                LogoutUserResult.Failure(response.message ?: "Непредвиденная ошибка удаления")
            }
        }catch (e: Exception){
            LogoutUserResult.Failure("Неизвестная ошибка: ${e.localizedMessage}")
        }
    }
}
