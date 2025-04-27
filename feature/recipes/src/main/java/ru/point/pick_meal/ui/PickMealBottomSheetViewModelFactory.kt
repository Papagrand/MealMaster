package ru.point.pick_meal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.recipe_information.domain.GetMealIdsUseCase
import ru.point.recipe_information.domain.AddRecipePortionToMealUseCase
import javax.inject.Provider
import javax.inject.Inject

class PickMealBottomSheetViewModelFactory @Inject constructor(
    private val getMealIdsUseCaseProvider: Provider<GetMealIdsUseCase>,
    private val addRecipePortionToMealUseCaseProvider: Provider<AddRecipePortionToMealUseCase>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == PickMealBottomSheetViewModel::class.java)
        return PickMealBottomSheetViewModel(
            getMealIdsUseCaseProvider.get(),
            addRecipePortionToMealUseCaseProvider.get()
        ) as T
    }

}