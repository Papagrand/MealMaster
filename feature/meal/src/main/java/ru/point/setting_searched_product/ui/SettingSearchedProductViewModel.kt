package ru.point.setting_searched_product.ui

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
import ru.point.api.daily_consumption_and_product.domain.models.ProductDataModel
import ru.point.meal.ui.MealProductSearchUiEvent
import ru.point.setting_searched_product.domain.AddProductToMealUseCase
import ru.point.setting_searched_product.domain.CalculateProductDataUseCase
import ru.point.setting_searched_product.domain.GetProductInformationUseCase



data class ProductDataUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val productData: ProductDataModel? = null
)

sealed class ProductDataUiEvent {
    data class ShowToast(val message: String) : ProductDataUiEvent()
    data object NavigateToMealProductSearch : ProductDataUiEvent()
}


class SettingSearchedProductViewModel(
    private val getProductInformationUseCase: GetProductInformationUseCase,
    private val calculateProductDataUseCase: CalculateProductDataUseCase,
    private val addProductToMealUseCase: AddProductToMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDataUiState())
    val uiState: StateFlow<ProductDataUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProductDataUiEvent>()
    val uiEvent: SharedFlow<ProductDataUiEvent> = _uiEvent.asSharedFlow()

    private var originalData: ProductDataModel? = null

    fun getProductInformation(productId: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = getProductInformationUseCase(productId)
            result.fold(
                onSuccess = { mealItemsResponse ->
                    originalData = mealItemsResponse.data
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        productData = mealItemsResponse.data
                    )
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(ProductDataUiEvent.ShowToast("Ошибка: ${ex.message}"))
                }
            )
        }
    }

    fun onServingSizeChanged(newSize: Double) {
        val orig = originalData ?: return
        viewModelScope.launch {
            val recalculated = calculateProductDataUseCase(orig, newServingSize = newSize)
            _uiState.value = _uiState.value.copy(productData = recalculated)
        }
    }

    fun onCaloriesChanged(newCalories: Double) {
        val orig = originalData ?: return
        viewModelScope.launch {
            val recalculated = calculateProductDataUseCase(orig, newCalories = newCalories)
            _uiState.value = _uiState.value.copy(productData = recalculated)
        }
    }

    fun addProductToMeal(mealId: String?, productId: String?){
        val servingSize = _uiState.value.productData?.productServingSizeDefault
        _uiState.value = _uiState.value.copy(isLoading = true)
        if (servingSize != null && mealId != null && productId != null){
            viewModelScope.launch {
                val result = addProductToMealUseCase(mealId, productId, servingSize)
                _uiState.value = _uiState.value.copy(isLoading = false)
                when(result){
                    is AddProductResult.Success -> {
                        _uiEvent.emit(ProductDataUiEvent.ShowToast("Продукт добавлен в текущий прием пищи"))
                    }
                    is AddProductResult.Failure -> {
                        _uiEvent.emit(ProductDataUiEvent.ShowToast(result.message))

                    }
                }
            }
        }
    }

    fun navigateToMealSearchFragment() {
        viewModelScope.launch {
            _uiEvent.emit(ProductDataUiEvent.NavigateToMealProductSearch)
        }
    }

}