package ru.point.api.meal.data

import ru.point.api.meal.domain.MealRepository
import ru.point.api.meal.domain.UpdateMealItemResult
import ru.point.api.meal.domain.models.MealItemsModel
import ru.point.api.meal.domain.models.MealSuccessModel
import ru.point.api.meal.domain.models.ProductItemModel
import ru.point.api.meal.domain.models.SearchedProductsSuccessModel

class MealRepositoryImpl(
    private val mealService: MealService
) : MealRepository {
    override suspend fun getMealInformationData(
        dailyConsumptionId: String,
        mealType: String
    ): Result<MealSuccessModel<MealItemsModel>> {
        return try {
            val response = mealService.getMealInformation(dailyConsumptionId, mealType)
            val body = response.body()
            when (response.code()) {
                200 -> {
                    if (body != null && body.success && body.data != null) {
                        val domainModel =
                            MealItemsModel(
                                mealId = body.data.mealId,
                                sumMealCarbohydrates = body.data.sumMealCarbohydrates,
                                sumMealProteins = body.data.sumMealProteins,
                                sumMealFats = body.data.sumMealFats,
                                sumMealCalories = body.data.sumMealCalories,
                                mealItemsList = body.data.mealItemsList
                            )
                        Result.success(MealSuccessModel(success = true, data = domainModel))
                    } else {
                        Result.failure(Throwable(body?.message ?: "Unexpected fault"))
                    }
                }

                else -> {
                    Result.failure(Throwable(body?.message ?: "Unexpected error"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchProducts(
        productName: String,
        isVegan: Boolean?,
        sortCalories: String?,
        page: Int?,
        pagesize: Int?
    ): Result<SearchedProductsSuccessModel<ProductItemModel>> {
        return try {
            val response = mealService.searchProducts(
                productName,
                isVegan == true,
                sortCalories ?: "null",
                page ?: 1,
                pagesize ?: 10
            )
            val body = response.body()
            when (response.code()) {
                200 -> {
                    if (body != null && body.success && body.data != null) {
                        val domainList: List<ProductItemModel> = body.data.map { dto ->
                            ProductItemModel(
                                productId = dto.productId,
                                productName = dto.productName,
                                productCalories = dto.productCalories,
                                productServingSizeDefault = dto.productServingSizeDefault,
                                productProtein = dto.productProtein,
                                productFat = dto.productFat,
                                productCarbohydrates = dto.productCarbohydrates,
                                productIsVegan = dto.productIsVegan,
                                productBackdrop = dto.productBackdrop
                            )
                        }
                        Result.success(
                            SearchedProductsSuccessModel(
                                success = true,
                                data = domainList
                            )
                        )
                    } else {
                        Result.failure(Throwable(body?.message ?: "Unexpected fault"))
                    }
                }

                404 -> {
                    Result.failure(Throwable(body?.message ?: "Not Found"))
                }

                else -> {
                    Result.failure(Throwable(body?.message ?: "Unexpected error"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProductInMeal(
        productItemId: String,
        newServingSize: Double
    ): UpdateMealItemResult {
        return try {
            val response: UpdateMealItemServingSizeCaloriesResponse = mealService.updateServingSizeOrCalories(
                UpdateMealItemServingSizeCaloriesRequest(productItemId, newServingSize)
            )
            if (response.success){
                UpdateMealItemResult.Success
            } else {
                UpdateMealItemResult.Failure(response.message ?: "Ошибка введенных данных")
            }
        } catch (e: Exception){
            UpdateMealItemResult.Failure("Неизвестная ошибка: ${e.localizedMessage}")
        }
    }

}