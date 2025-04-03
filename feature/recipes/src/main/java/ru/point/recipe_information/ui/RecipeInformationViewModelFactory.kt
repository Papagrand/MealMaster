package ru.point.recipe_information.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.recipe_information.domain.GetFullRecipeUseCase
import javax.inject.Inject
import javax.inject.Provider

class RecipeInformationViewModelFactory @Inject constructor(
    private val getFullRecipeUseCaseProvider: Provider<GetFullRecipeUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == RecipeInformationViewModel::class.java)
        return RecipeInformationViewModel(
            getFullRecipeUseCaseProvider.get()
        ) as T
    }
}