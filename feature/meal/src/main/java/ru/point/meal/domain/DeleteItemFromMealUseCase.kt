package ru.point.meal.domain

import ru.point.api.meal.domain.MealRepository
import ru.point.api.meal.domain.UpdateMealItemResult

class DeleteItemFromMealUseCase(
    private val mealRepository: MealRepository
) {

    suspend operator fun invoke(
        itemId: String
    ): UpdateMealItemResult {
        return mealRepository.deleteProductFromMeal(itemId)
    }

}