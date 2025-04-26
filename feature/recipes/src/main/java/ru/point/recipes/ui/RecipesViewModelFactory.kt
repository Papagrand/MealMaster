package ru.point.recipes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jakarta.inject.Provider
import ru.point.recipes.domain.GetSearchedRecipesUseCase
import javax.inject.Inject

class RecipesViewModelFactory @Inject constructor(
    private val getSearchedRecipesUseCaseProvider: Provider<GetSearchedRecipesUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == RecipesViewModel::class.java)
        return RecipesViewModel(
            getSearchedRecipesUseCaseProvider.get()
        ) as T
    }

}