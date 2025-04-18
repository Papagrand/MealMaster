package ru.point.setting_searched_product.domain

import ru.point.api.daily_consumption_and_product.domain.DailyConsumptionRepository
import ru.point.api.daily_consumption_and_product.domain.models.ProductDataModel
import ru.point.api.daily_consumption_and_product.domain.models.ProductSuccessModel

class GetProductInformationUseCase(
    private val dailyConsumptionRepository: DailyConsumptionRepository
) {
    suspend operator fun invoke(productId: String): Result<ProductSuccessModel<ProductDataModel>> {
        return dailyConsumptionRepository.getProductData(productId)
    }
}