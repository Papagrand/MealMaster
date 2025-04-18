package ru.point.meal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jakarta.inject.Provider
import ru.point.meal.domain.GetDailyConsumptionMealItemsUseCase
import ru.point.meal.domain.SearchProductsUseCase
import javax.inject.Inject

class MealProductSearchViewModelFactory @Inject constructor(
    private val getDailyConsumptionMealItemsUseCaseProvider: Provider<GetDailyConsumptionMealItemsUseCase>,
    private val searchProductsUseCaseProvider: Provider<SearchProductsUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == MealProductSearchViewModel::class.java)
        return MealProductSearchViewModel(
            getDailyConsumptionMealItemsUseCaseProvider.get(),
            searchProductsUseCaseProvider.get()
        ) as T
    }
}