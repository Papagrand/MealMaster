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
import ru.point.api.meal.domain.models.MealItemsModel
import ru.point.api.meal.domain.models.ProductItemModel
import ru.point.core.enums.SortCaloriesType
import ru.point.meal.domain.GetDailyConsumptionMealItemsUseCase
import ru.point.meal.domain.SearchProductsUseCase


data class SearchSettings(
    var searchText: String? = null,
    val pickedPage: Int? = 1,
    val pageSize: Int = 10,
    val isVegan: Boolean? = false,
    val sortCalories: String? = SortCaloriesType.OFF.type
)

data class MealProductSearchUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val mealData: MealItemsModel? = null,
    val searchedData: List<ProductItemModel>? = null
)

sealed class MealProductSearchUiEvent {
    data class ShowToast(val message: String) : MealProductSearchUiEvent()
    data object NavigateToHomeFragment : MealProductSearchUiEvent()
    data object NavigateToSearchedProductFragment : MealProductSearchUiEvent()

}

class MealProductSearchViewModel(
    private val getDailyConsumptionMealItemsUseCase: GetDailyConsumptionMealItemsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealProductSearchUiState())
    val uiState: StateFlow<MealProductSearchUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MealProductSearchUiEvent>()
    val uiEvent: SharedFlow<MealProductSearchUiEvent> = _uiEvent.asSharedFlow()

    private val _searchSettings = MutableStateFlow(SearchSettings())
    val searchSettings: StateFlow<SearchSettings> = _searchSettings.asStateFlow()

    fun updateSearchSettings(settings: SearchSettings) {
        _searchSettings.value = settings
    }

    fun getMealInformation(dailyConsumptionId: String, mealType: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = getDailyConsumptionMealItemsUseCase(dailyConsumptionId, mealType)
            result.fold(
                onSuccess = { mealItemsResponse ->
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        mealData = mealItemsResponse.data
                    )
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(MealProductSearchUiEvent.ShowToast("Ошибка: ${ex.message}"))
                }
            )
        }
    }

    fun getSearchedProducts(
        productName: String,
        page: Int? = _searchSettings.value.pickedPage,
        pageSize: Int = _searchSettings.value.pageSize,
        isVegan: Boolean? = _searchSettings.value.isVegan,
        sortCalories: String? = _searchSettings.value.sortCalories
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = searchProductsUseCase(
                productName = productName,
                isVegan = isVegan,
                sortCalories = sortCalories,
                page = page,
                pagesize = pageSize
            )
            result.fold(
                onSuccess = { searchedProductItemsResponse ->
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        searchedData = searchedProductItemsResponse.data
                    )
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(MealProductSearchUiEvent.ShowToast("Ошибка: ${ex.message}"))
                }
            )
        }
    }

    fun navigateToHomeFragment() {
        viewModelScope.launch {
            _uiEvent.emit(MealProductSearchUiEvent.NavigateToHomeFragment)
        }
    }

    fun navigateToSearchedProduct() {
        viewModelScope.launch {
            _uiEvent.emit(MealProductSearchUiEvent.NavigateToSearchedProductFragment)
        }
    }


}