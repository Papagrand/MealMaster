package ru.point.recipes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.recipes.domain.models.RecipeItemModel
import ru.point.core.enums.CookingTime
import ru.point.core.enums.DifficultyLevel
import ru.point.recipes.domain.GetSearchedRecipesUseCase

data class SearchRecipeSettings(
    var searchText: String? = null,
    val pickedPage: Int? = 1,
    val pageSize: Int = 10,
    val isVegan: Boolean? = false,
    val difficulty: Int? = DifficultyLevel.ANY.type,
    val cookingTime: Int? = CookingTime.ANY.type,
    val maxCalories: Double = 1000.0
)

data class RecipesSearchUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchedData: List<RecipeItemModel>? = null
)

sealed class RecipeSearchUiEvent {
    data class ShowSneaker(val message: String) : RecipeSearchUiEvent()
    data object NavigateToRecipeInformationFragment: RecipeSearchUiEvent()
}

class RecipesViewModel(
    private val getSearchedRecipesUseCase: GetSearchedRecipesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesSearchUiState())
    val uiState: StateFlow<RecipesSearchUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RecipeSearchUiEvent>()
    val uiEvent: SharedFlow<RecipeSearchUiEvent> = _uiEvent.asSharedFlow()

    private val _searchSettings = MutableStateFlow(SearchRecipeSettings())
    val searchSettings: StateFlow<SearchRecipeSettings> = _searchSettings.asStateFlow()

    fun updateSearchSettings(settings: SearchRecipeSettings){
        _searchSettings.value = settings
    }


    fun getSearchedRecipes(
        recipeName: String,
        page: Int? = _searchSettings.value.pickedPage,
        pageSize: Int = _searchSettings.value.pageSize,
        isVegan: Boolean? = _searchSettings.value.isVegan,
        difficulty: Int? = _searchSettings.value.difficulty,
        cookingTime: Int? = _searchSettings.value.cookingTime,
        maxCalories: Double? = _searchSettings.value.maxCalories
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = getSearchedRecipesUseCase(
                recipeName = recipeName,
                isVegan = isVegan,
                cookingTime = cookingTime,
                difficulty = difficulty,
                maxCalories = maxCalories,
                page = page,
                pagesize = pageSize
            )
            result.fold(
                onSuccess = { searchedRecipesItemsResponse ->
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        searchedData = searchedRecipesItemsResponse.data
                    )
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(RecipeSearchUiEvent.ShowSneaker("Ошибка: ${ex.message}"))
                }
            )
        }
    }

}