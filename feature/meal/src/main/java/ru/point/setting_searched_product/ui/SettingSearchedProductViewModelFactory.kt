package ru.point.setting_searched_product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jakarta.inject.Provider
import ru.point.setting_searched_product.domain.AddProductToMealUseCase
import ru.point.setting_searched_product.domain.CalculateProductDataUseCase
import ru.point.setting_searched_product.domain.GetProductInformationUseCase
import javax.inject.Inject

class SettingSearchedProductViewModelFactory @Inject constructor(
    private val getProductInformationUseCaseProvider: Provider<GetProductInformationUseCase>,
    private val calculateProductDataUseCaseProvider: Provider<CalculateProductDataUseCase>,
    private val addProductToMealUseCaseProvider: Provider<AddProductToMealUseCase>
    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SettingSearchedProductViewModel::class.java)
        return SettingSearchedProductViewModel(
            getProductInformationUseCaseProvider.get(),
            calculateProductDataUseCaseProvider.get(),
            addProductToMealUseCaseProvider.get()
        ) as T
    }
}