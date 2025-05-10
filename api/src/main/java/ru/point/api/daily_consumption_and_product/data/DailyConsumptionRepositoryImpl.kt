package ru.point.api.daily_consumption_and_product.data

import ru.point.api.daily_consumption_and_product.domain.AddProductResult
import ru.point.api.daily_consumption_and_product.domain.DailyConsumptionRepository
import ru.point.api.daily_consumption_and_product.domain.models.DailyConsumptionDataModel
import ru.point.api.daily_consumption_and_product.domain.models.DailyConsumptionSuccessModel
import ru.point.api.daily_consumption_and_product.domain.models.ProductDataModel
import ru.point.api.daily_consumption_and_product.domain.models.ProductSuccessModel

class DailyConsumptionRepositoryImpl(
    private val dailyConsumptionService: DailyConsumptionService
) : DailyConsumptionRepository {
    override suspend fun getDailyConsumptionMainData(
        userId: String,
        date: String
    ): Result<DailyConsumptionSuccessModel<DailyConsumptionDataModel>> {
        return try {
            val response = dailyConsumptionService.getDailyConsumption(userId, date)
            val body = response.body()
            when (response.code()) {
                200 -> {
                    if (body != null && body.success && body.data != null) {
                        val domainModel =
                            DailyConsumptionDataModel(
                                consumptionId = body.data.consumptionId,
                                userId = body.data.userId,
                                date = body.data.date,
                                totalCalories = body.data.totalCalories,
                                totalBreakfastCalories = body.data.totalBreakfastCalories,
                                totalLunchCalories = body.data.totalLunchCalories,
                                totalDinnerCalories = body.data.totalDinnerCalories,
                                totalSnackCalories = body.data.totalSnackCalories,
                                totalProteins = body.data.totalProteins,
                                totalCarbohydrates = body.data.totalCarbohydrates,
                                totalFats = body.data.totalFats,
                                totalDietaryFiber = body.data.totalDietaryFiber,
                                totalSugars = body.data.totalSugars,
                                totalStarchDextrins = body.data.totalStarchDextrins,
                                totalPotassium = body.data.totalPotassium,
                                totalCalcium = body.data.totalCalcium,
                                totalSilicon = body.data.totalSilicon,
                                totalMagnesium = body.data.totalMagnesium,
                                totalSodium = body.data.totalSodium,
                                totalSulfur = body.data.totalSulfur,
                                totalPhosphorus = body.data.totalPhosphorus,
                                totalChlorine = body.data.totalChlorine,
                                totalIron = body.data.totalIron,
                                totalZinc = body.data.totalZinc,
                                totalOmega3 = body.data.totalOmega3,
                                totalOmega6 = body.data.totalOmega6,
                                totalVitaminA = body.data.totalVitaminA,
                                totalVitaminB1 = body.data.totalVitaminB1,
                                totalVitaminB2 = body.data.totalVitaminB2,
                                totalVitaminB4 = body.data.totalVitaminB4,
                                totalVitaminC = body.data.totalVitaminC,
                                totalVitaminD = body.data.totalVitaminD,
                                totalBreakfastItemText = body.data.totalBreakfastItemText,
                                totalLunchItemText = body.data.totalLunchItemText,
                                totalDinnerItemText = body.data.totalDinnerItemText,
                                totalSnackItemText = body.data.totalSnackItemText
                            )
                        Result.success(
                            DailyConsumptionSuccessModel(
                                success = true,
                                data = domainModel
                            )
                        )
                    } else {
                        Result.failure(Throwable(body?.message ?: "Unexpected fault"))
                    }
                }

                403 -> {
                    Result.failure(Throwable(body?.message ?: "Data not found"))
                }

                502 -> {
                    Result.failure(Throwable(body?.message ?: "Network Error"))
                }

                else -> {
                    Result.failure(Throwable(body?.message ?: "Unexpected error"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductData(productId: String): Result<ProductSuccessModel<ProductDataModel>> {
        return try {
            val response = dailyConsumptionService.getProductById(productId)
            val body = response.body()
            when (response.code()) {
                200 -> {
                    if (body != null && body.success && body.data != null) {
                        val domainModel =
                            ProductDataModel(
                                productId = body.data.productId,
                                productName = body.data.productName,
                                productCalories = body.data.productCalories,
                                productServingSizeDefault = body.data.productServingSizeDefault,
                                productProtein = body.data.productProtein,
                                productFat = body.data.productFat,
                                productCarbohydrates = body.data.productCarbohydrates,
                                productDietaryFiber = body.data.productDietaryFiber,
                                productSugars = body.data.productSugars,
                                productStarchDextrins = body.data.productStarchDextrins,
                                productPotassium = body.data.productPotassium,
                                productCalcium = body.data.productCalcium,
                                productSilicon = body.data.productSilicon,
                                productMagnesium = body.data.productMagnesium,
                                productSodium = body.data.productSodium,
                                productSulfur = body.data.productSulfur,
                                productPhosphorus = body.data.productPhosphorus,
                                productChlorine = body.data.productChlorine,
                                productIron = body.data.productIron,
                                productZinc = body.data.productZinc,
                                productOmega3 = body.data.productOmega3,
                                productOmega6 = body.data.productOmega6,
                                productVitaminA = body.data.productVitaminA,
                                productVitaminB1 = body.data.productVitaminB1,
                                productVitaminB2 = body.data.productVitaminB2,
                                productVitaminB4 = body.data.productVitaminB4,
                                productVitaminC = body.data.productVitaminC,
                                productVitaminD = body.data.productVitaminD,
                                productIsVegan = body.data.productIsVegan,
                                productBackdrop = body.data.productBackdrop,
                                approved = body.data.approved
                            )
                        Result.success(ProductSuccessModel(success = true, data = domainModel))
                    } else {
                        Result.failure(Throwable(body?.message ?: "Unexpected fault"))
                    }
                }

                403 -> {
                    Result.failure(Throwable(body?.message ?: "Data not found"))
                }

                502 -> {
                    Result.failure(Throwable(body?.message ?: "Network Error"))
                }

                else -> {
                    Result.failure(Throwable(body?.message ?: "Unexpected error"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun addProductToMeal(
        mealId: String,
        productId: String,
        servingSize: Double
    ): Result<AddProductResult> {
        return try {
            val request = AddProductToMealRequest(
                mealId = mealId,
                productId = productId,
                servingSize = servingSize
            )

            val response = dailyConsumptionService.addProductToMeal(request)
            val body = response.body()
            when (response.code()) {
                201 ->{
                    if (body != null && body.success) {
                        Result.success(AddProductResult.Success)
                    } else {
                        val msg = body?.message ?: "Неизвестная ошибка сервера"
                        Result.success(AddProductResult.Failure(msg))
                    }
                }
                403 ->{
                    Result.failure(Throwable(body?.message ?: "Unexpected error"))
                }
                else -> {
                    Result.failure(Throwable(body?.message ?: "Unexpected error"))
                }
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

}