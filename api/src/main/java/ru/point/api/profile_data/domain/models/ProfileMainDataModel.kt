package ru.point.api.profile_data.domain.models

import kotlinx.serialization.Serializable


data class ProfileDataModel<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class ProfileMainDataModel(
    val userProfileId: String,
    val name: String,
    val secName: String?,
    val height: Double,
    val weight: Double,
    val age: Int,
    val gender: Boolean,
    val activityLevel: String,
    val goal: String,
    val goalWeight: Double,
    val goalTimeStart: String,
    val goalTimeEnd: String,
    val profilePicture: String,
)


@Serializable
data class MainMaxNutrientsDataModel(
    val maxCalories: Double,
    val maxBreakfastCalories: Double,
    val maxLunchCalories: Double,
    val maxDinnerCalories: Double,
    val maxSnackCalories: Double,
    val maxProtein: Double,
    val maxCarbohydrates: Double,
    val maxFat: Double,
    val maxDietaryFiber: Double,
    val maxSugars: Double,
    val maxStarchDextrins: Double,
    val maxPotassium: Double,
    val maxCalcium: Double,
    val maxSilicon: Double,
    val maxMagnesium: Double,
    val maxSodium: Double,
    val maxSulfur: Double,
    val maxPhosphorus: Double,
    val maxChlorine: Double,
    val maxIron: Double,
    val maxZinc: Double,
    val maxOmega3: Double,
    val maxOmega6: Double,
    val maxVitaminA: Double,
    val maxVitaminB1: Double,
    val maxVitaminB2: Double,
    val maxVitaminB4: Double,
    val maxVitaminC: Double,
    val maxVitaminD: Double,
)