package ru.point.meal.domain

import ru.point.api.meal.domain.MealRepository
import ru.point.api.meal.domain.UpdateMealItemResult

class UpdateItemMealUseCase(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(
        productItemId: String,
        newServingSize: Double
    ): UpdateMealItemResult {
        return mealRepository.updateProductInMeal(productItemId, newServingSize)
    }

}