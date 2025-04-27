package ru.point.pick_meal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.daily_consumption_and_product.domain.AddProductResult
import ru.point.api.recipes.domain.models.MealIdsInfoModel
import ru.point.recipe_information.domain.GetMealIdsUseCase
import ru.point.recipe_information.domain.AddRecipePortionToMealUseCase

data class PickMealUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val mealIdsData: List<MealIdsInfoModel>? = null
)

sealed class PickMealUiEvent {
    data class ShowToast(val message: String) : PickMealUiEvent()
    data object NavigateBack : PickMealUiEvent()
}

class PickMealBottomSheetViewModel(
    private val getMealIdsUseCase: GetMealIdsUseCase,
    private val addRecipePortionToMealUseCase: AddRecipePortionToMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickMealUiState())
    val uiState: StateFlow<PickMealUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<PickMealUiEvent>()
    val uiEvent: SharedFlow<PickMealUiEvent> = _uiEvent.asSharedFlow()

    fun getMealIds(userId: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = getMealIdsUseCase(userId)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null,
                        mealIdsData = response.data
                    )
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(PickMealUiEvent.ShowToast("Ошибка: ${ex.message}"))
                }
            )
        }
    }

    fun addProductToMeal(mealId: String?, productId: String?, servingSize: Double){
        _uiState.value = _uiState.value.copy(isLoading = true)
        if (servingSize != null && mealId != null && productId != null){
            viewModelScope.launch {
                val result = addRecipePortionToMealUseCase(mealId, productId, servingSize)
                _uiState.value = _uiState.value.copy(isLoading = false)
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                        _uiEvent.emit(PickMealUiEvent.ShowToast("Продукт добавлен в выбранный прием пищи"))
                        _uiEvent.emit(PickMealUiEvent.NavigateBack)
                    },
                    onFailure = { ex ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = ex.message
                        )
                        _uiEvent.emit(PickMealUiEvent.ShowToast("Ошибка: ${ex.message}"))
                    }

                )
//                when(result){
//                    is AddProductResult.Success -> {
//                        _uiEvent.emit(PickMealUiEvent.ShowToast("Продукт добавлен в текущий прием пищи"))
//                        _uiEvent.emit(PickMealUiEvent.NavigateBack)
//                    }
//                    is AddProductResult.Failure -> {
//                        _uiEvent.emit(PickMealUiEvent.ShowToast(result.message))
//
//                    }
//                }
            }
        }
    }

    fun sendUiEvent(event: PickMealUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

}