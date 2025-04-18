package ru.point.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.daily_consumption_and_product.domain.models.DailyConsumptionDataModel
import ru.point.api.profile_data.domain.models.MainMaxNutrientsDataModel
import ru.point.home.domain.GetDailyConsumptionUseCase
import ru.point.home.domain.GetMainMaxNutrientsDataUseCase

data class HomeProgressUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val nutrientsData: MainMaxNutrientsDataModel? = null,
    val consumptionData: DailyConsumptionDataModel? = null
)

sealed class HomeProgressUiEvent {
    data class ShowToast(val message: String) : HomeProgressUiEvent()
    data object NavigateToBreakfastMeal : HomeProgressUiEvent()
    data object NavigateToLunchMeal : HomeProgressUiEvent()
    data object NavigateToDinnerMeal : HomeProgressUiEvent()
    data object NavigateToSnackMeal : HomeProgressUiEvent()
}

class HomeProgressViewModel(
    private val getMainMaxNutrientsDataUseCase: GetMainMaxNutrientsDataUseCase,
    private val getDailyConsumptionUseCase: GetDailyConsumptionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeProgressUiState())
    val uiState: StateFlow<HomeProgressUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeProgressUiEvent>()
    val uiEvent: SharedFlow<HomeProgressUiEvent> = _uiEvent.asSharedFlow()

    fun getMaxNutrientsData(userProfileId: String){
        viewModelScope.launch{
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = getMainMaxNutrientsDataUseCase(userProfileId)
            result.fold(
                onSuccess = { nutrientsResponse ->
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        nutrientsData = nutrientsResponse.data
                    )
                },
                onFailure = {ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(HomeProgressUiEvent.ShowToast("Ошибка: ${ex.message}"))
                }
            )
        }
    }

    fun getDailyConsumptionData(userId: String, date: String){
        viewModelScope.launch{
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = getDailyConsumptionUseCase(userId, date)
            result.fold(
                onSuccess = { dailyConsumptionResponse ->
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        consumptionData = dailyConsumptionResponse.data
                    )
                },
                onFailure = {ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(HomeProgressUiEvent.ShowToast("Ошибка: ${ex.message}"))
                }
            )

        }
    }

    fun navigateToBreakfastMeal() {
        viewModelScope.launch {
            _uiEvent.emit(HomeProgressUiEvent.NavigateToBreakfastMeal)
        }
    }

    fun navigateToLunchMeal() {
        viewModelScope.launch {
            _uiEvent.emit(HomeProgressUiEvent.NavigateToLunchMeal)
        }
    }

    fun navigateToDinnerMeal() {
        viewModelScope.launch {
            _uiEvent.emit(HomeProgressUiEvent.NavigateToDinnerMeal)
        }
    }

    fun navigateToSnackMeal() {
        viewModelScope.launch {
            _uiEvent.emit(HomeProgressUiEvent.NavigateToSnackMeal)
        }
    }

}