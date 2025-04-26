package ru.point.api.recipes.domain

import ru.point.api.recipes.domain.models.FullRecipeData
import ru.point.api.recipes.domain.models.FullResponseRecipeData
import ru.point.api.recipes.domain.models.RecipeItemModel
import ru.point.api.recipes.domain.models.SearchedRecipesSuccessModel

interface RecipeRepository {
    suspend fun getFullRecipeData(recipeId: String): Result<FullResponseRecipeData<FullRecipeData>>

    suspend fun searchRecipes(
        recipeName: String,
        isVegan: Boolean?,
        cookingTime: Int?,
        difficulty: Int?,
        maxCalories: Double?,
        page: Int?,
        pagesize: Int?
    ): Result<SearchedRecipesSuccessModel<RecipeItemModel>>
}