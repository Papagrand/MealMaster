package ru.point.fasting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.fasting.domain.AdjustStartUseCase
import ru.point.fasting.domain.GetScenarioUseCase
import ru.point.fasting.domain.GetTimerUseCase
import ru.point.fasting.domain.Scenario
import ru.point.fasting.domain.StartTimerUseCase
import ru.point.fasting.domain.StopTimerUseCase
import ru.point.fasting.domain.TogglePhaseUseCase
import ru.point.fasting.domain.UserTimer

class FastingViewModel(
    private val getTimerUseCase: GetTimerUseCase,
    private val getScenarioUseCase: GetScenarioUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val stopTimerUseCase: StopTimerUseCase,
    private val adjustStartUseCase: AdjustStartUseCase,
    private val togglePhaseUseCase: TogglePhaseUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<UserTimer?>(null)
    val uiState: StateFlow<UserTimer?> = _uiState.asStateFlow()

    fun loadTimer(userId: String) {
        viewModelScope.launch {

            getTimerUseCase.initialFetch(userId)

            getTimerUseCase(userId)
                .collect { timer ->
                    _uiState.value = timer
                }
        }
    }

    fun updateBackendFastingInfo(){
        viewModelScope.launch {

        }
    }

    fun onToggleStartSwitch(newStart: Long, userId: String) = viewModelScope.launch {
        _uiState.value?.let { timer ->
            if (!timer.isActive) {
                startTimerUseCase(timer.userId)
            } else {
                togglePhaseUseCase(timer.userId, newStart)
            }
        }
        getTimerUseCase(userId)
            .collect { timer ->
                _uiState.value = timer
            }
    }

    fun onStop(userId: String) = viewModelScope.launch {
        stopTimerUseCase(userId)

        getTimerUseCase(userId)
            .collect { timer ->
                _uiState.value = timer
            }
    }

    fun onAdjustStart(newStart: Long, userId: String) = viewModelScope.launch {
        _uiState.value?.let { timer ->
            adjustStartUseCase(timer.userId, newStart)
        }
        getTimerUseCase(userId)
            .collect { timer ->
                _uiState.value = timer
            }
    }

}