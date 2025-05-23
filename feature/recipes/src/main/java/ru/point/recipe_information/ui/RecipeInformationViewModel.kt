package ru.point.recipe_information.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.recipes.domain.models.FullRecipeData
import ru.point.recipe_information.domain.GetFullRecipeUseCase
import ru.point.recipe_information.domain.CalculateRecipeDataUseCase


data class RecipeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val recipeData: FullRecipeData? = null
)

sealed class RecipeUiEvent {
    data class ShowToast(val message: String) : RecipeUiEvent()
    data object NavigateBack : RecipeUiEvent()
}

class RecipeInformationViewModel(
    private val getFullRecipeUseCase: GetFullRecipeUseCase,
    private val calculateRecipeDataUseCase: CalculateRecipeDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RecipeUiEvent>()
    val uiEvent: SharedFlow<RecipeUiEvent> = _uiEvent.asSharedFlow()


    private var originalData: FullRecipeData? = null


    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = getFullRecipeUseCase(recipeId)
            result.fold(
                onSuccess = { fullResponse ->
                    originalData = fullResponse.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null,
                        recipeData = fullResponse.data
                    )
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(RecipeUiEvent.ShowToast("Ошибка: ${ex.message}"))
                }
            )
        }
    }

    fun onServingSizeChanged(newSize: Double) {
        val orig = originalData ?: return
        viewModelScope.launch {
            val recalculated = calculateRecipeDataUseCase(orig, newServingSize = newSize)
            _uiState.value = _uiState.value.copy(recipeData = recalculated)
        }
    }

    fun onCaloriesChanged(newCalories: Double) {
        val orig = originalData ?: return
        viewModelScope.launch {
            val recalculated = calculateRecipeDataUseCase(orig, newCalories = newCalories)
            _uiState.value = _uiState.value.copy(recipeData = recalculated)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.emit(RecipeUiEvent.NavigateBack)
        }
    }
}
