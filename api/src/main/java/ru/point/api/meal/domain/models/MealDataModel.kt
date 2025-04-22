package ru.point.api.meal.domain.models

import kotlinx.serialization.Serializable

data class MealSuccessModel<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class MealItemsModel (
    val mealId: String,
    val sumMealCarbohydrates: Double,
    val sumMealProteins: Double,
    val sumMealFats: Double,
    val sumMealCalories: Double,
    val mealItemsList: List<FoodItemModel>
)

data class SearchedProductsSuccessModel<T>(
    val success: Boolean,
    val data: List<T>? = null,
    val message: String? = null
)

@Serializable
data class ProductItemModel (
    val productId: String,
    val productName: String,
    val productCalories: Double,
    val productServingSizeDefault: Double,
    val productProtein: Double,
    val productFat: Double,
    val productCarbohydrates: Double,
    val productIsVegan: Boolean,
    val productBackdrop: String
)

@Serializable
data class FoodItemModel(
    val itemId: String,
    val mealId: String,
    val productId: String,
    val itemName: String,
    val itemServingSize: Double,
    val isProduct: Boolean,
    val itemCalories: Double,
    val itemProtein: Double,
    val itemFat: Double,
    val itemCarbohydrates: Double,
    val itemDietaryFiber: Double,
    val itemSugars: Double,
    val itemStarchDextrins: Double,
    val itemPotassium: Double,
    val itemCalcium: Double,
    val itemSilicon: Double,
    val itemMagnesium: Double,
    val itemSodium: Double,
    val itemSulfur: Double,
    val itemPhosphorus: Double,
    val itemChlorine: Double,
    val itemIron: Double,
    val itemZinc: Double,
    val itemOmega3: Double,
    val itemOmega6: Double,
    val itemVitaminA: Double,
    val itemVitaminB1: Double,
    val itemVitaminB2: Double,
    val itemVitaminB4: Double,
    val itemVitaminC: Double,
    val itemVitaminD: Double
)