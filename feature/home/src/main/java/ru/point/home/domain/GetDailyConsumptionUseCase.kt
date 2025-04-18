package ru.point.home.domain

import ru.point.api.daily_consumption_and_product.domain.DailyConsumptionRepository
import ru.point.api.daily_consumption_and_product.domain.models.DailyConsumptionDataModel
import ru.point.api.daily_consumption_and_product.domain.models.DailyConsumptionSuccessModel

class GetDailyConsumptionUseCase (
    private val dailyConsumptionRepository: DailyConsumptionRepository
) {
    suspend operator fun invoke(userId: String, date: String): Result<DailyConsumptionSuccessModel<DailyConsumptionDataModel>>{
        return dailyConsumptionRepository.getDailyConsumptionMainData(userId, date)
    }
}