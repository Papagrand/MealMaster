package ru.point.setting_searched_product.domain

import ru.point.api.daily_consumption_and_product.domain.AddProductResult
import ru.point.api.daily_consumption_and_product.domain.DailyConsumptionRepository

class AddProductToMealUseCase (
    private val dailyConsumptionRepository: DailyConsumptionRepository
){
    suspend operator fun invoke(mealId: String, productId: String, servingSize: Double): AddProductResult {
        return dailyConsumptionRepository.addProductToMeal(mealId, productId, servingSize)
    }
}