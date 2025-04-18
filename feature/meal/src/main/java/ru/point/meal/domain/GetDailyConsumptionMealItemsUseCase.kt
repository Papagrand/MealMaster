package ru.point.meal.domain

import ru.point.api.meal.domain.MealRepository
import ru.point.api.meal.domain.models.MealItemsModel
import ru.point.api.meal.domain.models.MealSuccessModel

class GetDailyConsumptionMealItemsUseCase (
    private val mealRepository: MealRepository
) {

    suspend operator fun invoke(dailyConsumptionId: String, mealType: String): Result<MealSuccessModel<MealItemsModel>>{
        return mealRepository.getMealInformationData(dailyConsumptionId, mealType)
    }

}