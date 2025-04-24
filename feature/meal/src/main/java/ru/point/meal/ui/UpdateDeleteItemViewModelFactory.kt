package ru.point.meal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.meal.domain.CalculateItemDataUseCase
import ru.point.meal.domain.DeleteItemFromMealUseCase
import ru.point.meal.domain.UpdateItemMealUseCase
import javax.inject.Inject
import javax.inject.Provider

class UpdateDeleteItemViewModelFactory @Inject constructor(
    private val updateItemMealUseCaseProvider: Provider<UpdateItemMealUseCase>,
    private val calculateItemDataUseCaseProvider: Provider<CalculateItemDataUseCase>,
    private val deleteItemFromMealUseCaseProvider: Provider<DeleteItemFromMealUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == UpdateDeleteItemViewModel::class.java)
        return UpdateDeleteItemViewModel(
            updateItemMealUseCaseProvider.get(),
            calculateItemDataUseCaseProvider.get(),
            deleteItemFromMealUseCaseProvider.get()
        ) as T
    }
}