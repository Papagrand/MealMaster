package ru.point.meal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.meal.domain.UpdateItemMealUseCase
import javax.inject.Inject
import javax.inject.Provider

class UpdateDeleteItemViewModelFactory @Inject constructor(
    private val updateItemMealUseCaseProvider: Provider<UpdateItemMealUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == UpdateDeleteItemViewModel::class.java)
        return UpdateDeleteItemViewModel(
            updateItemMealUseCaseProvider.get()
        ) as T
    }
}