package ru.point.recipe_information.domain

import ru.point.api.recipes.domain.RecipeRepository
import ru.point.api.recipes.domain.models.MealIdsInfoModel
import ru.point.api.recipes.domain.models.MealIdsModel

class GetMealIdsUseCase (
    private val recipeRepository: RecipeRepository
) {

    suspend operator fun invoke(userId: String): Result<MealIdsModel<MealIdsInfoModel>>{
        return recipeRepository.getMealIds(userId)
    }

}