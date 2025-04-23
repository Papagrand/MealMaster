package ru.point.meal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.meal.domain.UpdateMealItemResult
import ru.point.meal.domain.UpdateItemMealUseCase

data class UpdateDeleteItemUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class UpdateDeleteItemUiEvent {
    data class ShowSneaker(val message: String) : UpdateDeleteItemUiEvent()
}

class UpdateDeleteItemViewModel(
    private val updateItemMealUseCase: UpdateItemMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateDeleteItemUiState())
    val uiState: StateFlow<UpdateDeleteItemUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UpdateDeleteItemUiEvent>()
    val uiEvent: SharedFlow<UpdateDeleteItemUiEvent> = _uiEvent.asSharedFlow()


    fun updateItemMealServingSize(productItemId: String, newServingSize: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = updateItemMealUseCase(productItemId, newServingSize)
            when (result) {
                is UpdateMealItemResult.Success -> {
                    _uiEvent.emit(UpdateDeleteItemUiEvent.ShowSneaker("Данные успешно обновлены"))
                }

                is UpdateMealItemResult.Failure -> {
                    _uiEvent.emit(UpdateDeleteItemUiEvent.ShowSneaker(result.message))
                }
            }
        }
    }
}