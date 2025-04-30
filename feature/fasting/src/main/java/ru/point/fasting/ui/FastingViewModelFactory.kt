package ru.point.fasting.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import ru.point.fasting.domain.AdjustStartUseCase
import ru.point.fasting.domain.GetTimerUseCase
import ru.point.fasting.domain.StartTimerUseCase
import ru.point.fasting.domain.StopTimerUseCase
import javax.inject.Provider
import javax.inject.Inject

class FastingViewModelFactory @Inject constructor(
    private val getTimerUseCaseProvider: Provider<GetTimerUseCase>,
    private val startTimerUseCaseProvider: Provider<StartTimerUseCase>,
    private val stopTimerUseCaseProvider: Provider<StopTimerUseCase>,
    private val adjustStartUseCaseProvider: Provider<AdjustStartUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == FastingViewModel::class.java)
        return FastingViewModel(
            getTimerUseCaseProvider.get(),
            startTimerUseCaseProvider.get(),
            stopTimerUseCaseProvider.get(),
            adjustStartUseCaseProvider.get()
        ) as T
    }
}
