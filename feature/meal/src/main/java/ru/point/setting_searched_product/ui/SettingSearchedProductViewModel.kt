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
import ru.point.api.daily_consumption_and_product.domain.models.ProductDataModel
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
    private val getProductInformationUseCase: GetProductInformationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDataUiState())
    val uiState: StateFlow<ProductDataUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProductDataUiEvent>()
    val uiEvent: SharedFlow<ProductDataUiEvent> = _uiEvent.asSharedFlow()

    fun getProductInformation(productId: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = getProductInformationUseCase(productId)
            result.fold(
                onSuccess = { mealItemsResponse ->
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

}