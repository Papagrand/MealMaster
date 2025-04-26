package ru.point.recipes.domain

import ru.point.api.recipes.domain.RecipeRepository
import ru.point.api.recipes.domain.models.RecipeItemModel
import ru.point.api.recipes.domain.models.SearchedRecipesSuccessModel

class GetSearchedRecipesUseCase(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(
        recipeName: String,
        isVegan: Boolean?,
        cookingTime: Int?,
        difficulty: Int?,
        maxCalories: Double?,
        page: Int?,
        pagesize: Int?
    ): Result<SearchedRecipesSuccessModel<RecipeItemModel>> {
        return recipeRepository.searchRecipes(
            recipeName, isVegan, cookingTime, difficulty, maxCalories, page, pagesize
        )
    }
}