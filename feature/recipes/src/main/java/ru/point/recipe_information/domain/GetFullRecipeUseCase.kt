package ru.point.recipe_information.domain

import ru.point.api.recipes.domain.RecipeRepository
import ru.point.api.recipes.domain.models.FullRecipeData
import ru.point.api.recipes.domain.models.FullResponseRecipeData

class GetFullRecipeUseCase(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: String): Result<FullResponseRecipeData<FullRecipeData>> {
        return recipeRepository.getFullRecipeData(recipeId)
    }
}