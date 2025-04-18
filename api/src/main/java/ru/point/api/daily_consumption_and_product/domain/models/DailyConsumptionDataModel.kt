package ru.point.api.daily_consumption_and_product.domain.models

import kotlinx.serialization.Serializable


data class DailyConsumptionSuccessModel<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class DailyConsumptionDataModel(
    val consumptionId: String,
    val userId: String,
    val date: String,
    val totalCalories: Double,
    val totalBreakfastCalories: Double,
    val totalLunchCalories: Double,
    val totalDinnerCalories: Double,
    val totalSnackCalories: Double,
    val totalProteins: Double,
    val totalCarbohydrates: Double,
    val totalFats: Double,
    val totalDietaryFiber: Double,
    val totalSugars: Double,
    val totalStarchDextrins: Double,
    val totalPotassium: Double,
    val totalCalcium: Double,
    val totalSilicon: Double,
    val totalMagnesium: Double,
    val totalSodium: Double,
    val totalSulfur: Double,
    val totalPhosphorus: Double,
    val totalChlorine: Double,
    val totalIron: Double,
    val totalZinc: Double,
    val totalOmega3: Double,
    val totalOmega6: Double,
    val totalVitaminA: Double,
    val totalVitaminB1: Double,
    val totalVitaminB2: Double,
    val totalVitaminB4: Double,
    val totalVitaminC: Double,
    val totalVitaminD: Double
)

@Serializable
data class ProductSuccessModel<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class ProductDataModel(
    val productId: String,
    val productName: String,
    val productCalories: Double,
    val productServingSizeDefault: Double,
    val productProtein: Double,
    val productFat: Double,
    val productCarbohydrates: Double,
    val productDietaryFiber: Double,
    val productSugars: Double,
    val productStarchDextrins: Double,
    val productPotassium: Double,
    val productCalcium: Double,
    val productSilicon: Double,
    val productMagnesium: Double,
    val productSodium: Double,
    val productSulfur: Double,
    val productPhosphorus: Double,
    val productChlorine: Double,
    val productIron: Double,
    val productZinc: Double,
    val productOmega3: Double,
    val productOmega6: Double,
    val productVitaminA: Double,
    val productVitaminB1: Double,
    val productVitaminB2: Double,
    val productVitaminB4: Double,
    val productVitaminC: Double,
    val productVitaminD: Double,
    val productIsVegan: Boolean,
    val productBackdrop: String,
    val approved: Boolean,
)
