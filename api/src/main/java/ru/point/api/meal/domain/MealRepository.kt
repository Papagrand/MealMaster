package ru.point.api.meal.domain

import ru.point.api.meal.domain.models.MealItemsModel
import ru.point.api.meal.domain.models.MealSuccessModel
import ru.point.api.meal.domain.models.ProductItemModel
import ru.point.api.meal.domain.models.SearchedProductsSuccessModel

interface MealRepository {
    suspend fun getMealInformationData(
        dailyConsumptionId: String,
        mealType: String
    ): Result<MealSuccessModel<MealItemsModel>>

    suspend fun searchProducts(
        productName: String,
        isVegan: Boolean?,
        sortCalories: String?,
        page: Int?,
        pagesize: Int?
    ): Result<SearchedProductsSuccessModel<ProductItemModel>>

    suspend fun updateProductInMeal(productItemId: String, newServingSize: Double): UpdateMealItemResult

}