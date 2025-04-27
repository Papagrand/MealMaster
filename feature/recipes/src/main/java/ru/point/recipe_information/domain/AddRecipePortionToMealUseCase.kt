package ru.point.recipe_information.domain

import ru.point.api.daily_consumption_and_product.domain.AddProductResult
import ru.point.api.daily_consumption_and_product.domain.DailyConsumptionRepository
import javax.inject.Inject

class AddRecipePortionToMealUseCase  @Inject constructor(
    private val dailyConsumptionRepository: DailyConsumptionRepository
){
    suspend operator fun invoke(mealId: String, productId: String, servingSize: Double): Result<AddProductResult> {
        return dailyConsumptionRepository.addProductToMeal(mealId, productId, servingSize)
    }
}