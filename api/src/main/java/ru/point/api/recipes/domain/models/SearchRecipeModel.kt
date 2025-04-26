package ru.point.api.recipes.domain.models

import kotlinx.serialization.Serializable

data class SearchedRecipesSuccessModel<T>(
    val success: Boolean,
    val data: List<T>? = null,
    val message: String? = null
)

@Serializable
data class RecipeItemModel (
    val recipeId: String,
    val recipeName: String,
    val recipeBackdrop: String,
    val recipeCookingTime: Int,
    val recipeDifficulty: Int,
    val recipeIsVegan: Boolean,
    val recipeServingSize: Double,
    val recipeCalories: Double,
    val recipeProtein: Double,
    val recipeFat: Double,
    val recipeCarbohydrate: Double
)