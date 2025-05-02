package ru.point.fasting.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import ru.point.fasting.domain.AdjustStartUseCase
import ru.point.fasting.domain.GetScenarioUseCase
import ru.point.fasting.domain.GetTimerUseCase
import ru.point.fasting.domain.StartTimerUseCase
import ru.point.fasting.domain.StopTimerUseCase
import ru.point.fasting.domain.TogglePhaseUseCase
import javax.inject.Provider
import javax.inject.Inject

class FastingViewModelFactory @Inject constructor(
    private val getTimerUseCaseProvider: Provider<GetTimerUseCase>,
    private val getScenarioUseCaseProvider: Provider<GetScenarioUseCase>,
    private val startTimerUseCaseProvider: Provider<StartTimerUseCase>,
    private val stopTimerUseCaseProvider: Provider<StopTimerUseCase>,
    private val adjustStartUseCaseProvider: Provider<AdjustStartUseCase>,
    private val togglePhaseUseCaseProvider: Provider<TogglePhaseUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == FastingViewModel::class.java)
        return FastingViewModel(
            getTimerUseCaseProvider.get(),
            getScenarioUseCaseProvider.get(),
            startTimerUseCaseProvider.get(),
            stopTimerUseCaseProvider.get(),
            adjustStartUseCaseProvider.get(),
            togglePhaseUseCaseProvider.get()
        ) as T
    }
}
