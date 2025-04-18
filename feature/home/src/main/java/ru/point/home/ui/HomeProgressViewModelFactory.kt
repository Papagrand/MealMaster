package ru.point.home.ui

import javax.inject.Inject
import javax.inject.Provider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.home.domain.GetDailyConsumptionUseCase
import ru.point.home.domain.GetMainMaxNutrientsDataUseCase

class HomeProgressViewModelFactory @Inject constructor(
    private val getMainMaxNutrientsDataUseCaseProvider: Provider<GetMainMaxNutrientsDataUseCase>,
    private val getDailyConsumptionUseCaseProvider: Provider<GetDailyConsumptionUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == HomeProgressViewModel::class.java)
        return HomeProgressViewModel(
            getMainMaxNutrientsDataUseCaseProvider.get(),
            getDailyConsumptionUseCaseProvider.get()
        ) as T
    }
}