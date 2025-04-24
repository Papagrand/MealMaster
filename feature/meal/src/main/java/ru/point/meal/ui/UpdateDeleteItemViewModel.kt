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
import ru.point.api.meal.data.ItemInMealDataModel
import ru.point.api.meal.domain.UpdateMealItemResult
import ru.point.meal.domain.CalculateItemDataUseCase
import ru.point.meal.domain.DeleteItemFromMealUseCase
import ru.point.meal.domain.UpdateItemMealUseCase

data class UpdateDeleteItemUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val itemData: ItemInMealDataModel? = null
)

sealed class UpdateDeleteItemUiEvent {
    data class ShowSneaker(val message: String) : UpdateDeleteItemUiEvent()
}

class UpdateDeleteItemViewModel(
    private val updateItemMealUseCase: UpdateItemMealUseCase,
    private val calculateItemDataUseCase: CalculateItemDataUseCase,
    private val deleteItemFromMealUseCase: DeleteItemFromMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateDeleteItemUiState())
    val uiState: StateFlow<UpdateDeleteItemUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UpdateDeleteItemUiEvent>()
    val uiEvent: SharedFlow<UpdateDeleteItemUiEvent> = _uiEvent.asSharedFlow()

    private var currentData: ItemInMealDataModel? = null


    fun insertCurrentData(itemId: String, servingSize: Double, calories: Double) {
        viewModelScope.launch {
            val newData = ItemInMealDataModel(
                itemId = itemId,
                currentServingSize = servingSize,
                currentCalories = calories
            )
            currentData = newData
            _uiState.value = _uiState.value.copy(itemData = currentData)
        }
    }

    fun updateItemMealServingSize(safetyServingSize: Double) {

        val itemId = currentData?.itemId

        val servingSize = _uiState.value.itemData?.currentServingSize

        if (itemId != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val result = updateItemMealUseCase(itemId, servingSize ?: safetyServingSize)
                when (result) {
                    is UpdateMealItemResult.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _uiEvent.emit(UpdateDeleteItemUiEvent.ShowSneaker("Данные успешно обновлены"))
                    }

                    is UpdateMealItemResult.Failure -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _uiEvent.emit(UpdateDeleteItemUiEvent.ShowSneaker(result.message))
                    }
                }
            }
        }
    }

    fun deleteItemFromMeal() {
        val itemId = currentData?.itemId
        if (itemId != null) {
            viewModelScope.launch {
                val result = deleteItemFromMealUseCase(itemId)
                when(result){
                    is UpdateMealItemResult.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _uiEvent.emit(UpdateDeleteItemUiEvent.ShowSneaker("Продукт удален"))
                    }

                    is UpdateMealItemResult.Failure -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _uiEvent.emit(UpdateDeleteItemUiEvent.ShowSneaker(result.message))
                    }
                }
            }
        }

    }

    fun onServingSizeChanged(newServingSize: Double) {
        val current = currentData ?: return
        viewModelScope.launch {
            val recalculated = calculateItemDataUseCase(current, newServingSize = newServingSize)
            _uiState.value = _uiState.value.copy(itemData = recalculated)
        }
    }

    fun onCaloriesChanged(newCalories: Double) {
        val current = currentData ?: return
        viewModelScope.launch {
            val recalculated = calculateItemDataUseCase(current, newCalories = newCalories)
            _uiState.value = _uiState.value.copy(itemData = recalculated)
        }
    }

}