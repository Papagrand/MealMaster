package ru.point.api.recipes.domain

import ru.point.api.recipes.domain.models.FullRecipeData
import ru.point.api.recipes.domain.models.FullResponseRecipeData

interface RecipeRepository {
    suspend fun getFullRecipeData(recipeId: String): Result<FullResponseRecipeData<FullRecipeData>>
}