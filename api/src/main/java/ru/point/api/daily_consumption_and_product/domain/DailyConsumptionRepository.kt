package ru.point.api.daily_consumption_and_product.domain

import ru.point.api.daily_consumption_and_product.domain.models.DailyConsumptionDataModel
import ru.point.api.daily_consumption_and_product.domain.models.DailyConsumptionSuccessModel
import ru.point.api.daily_consumption_and_product.domain.models.ProductDataModel
import ru.point.api.daily_consumption_and_product.domain.models.ProductSuccessModel
import ru.point.api.meal.domain.models.ProductItemModel

interface DailyConsumptionRepository {
    suspend fun getDailyConsumptionMainData(
        userId: String,
        date: String
    ): Result<DailyConsumptionSuccessModel<DailyConsumptionDataModel>>

    suspend fun getProductData(
        productId: String
    ): Result<ProductSuccessModel<ProductDataModel>>
}