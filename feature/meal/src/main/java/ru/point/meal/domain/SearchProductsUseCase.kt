package ru.point.meal.domain

import ru.point.api.meal.domain.MealRepository
import ru.point.api.meal.domain.models.ProductItemModel
import ru.point.api.meal.domain.models.SearchedProductsSuccessModel

class SearchProductsUseCase(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(
        productName: String,
        isVegan: Boolean?,
        sortCalories: String?,
        page: Int?,
        pagesize: Int?
    ): Result<SearchedProductsSuccessModel<ProductItemModel>> {
        return mealRepository.searchProducts(
            productName, isVegan, sortCalories, page, pagesize
        )
    }
}