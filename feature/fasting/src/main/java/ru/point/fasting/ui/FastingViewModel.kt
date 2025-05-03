package ru.point.fasting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.timer_fasting.domain.UpdateFastingBackendInfoResult
import ru.point.fasting.domain.AdjustStartUseCase
import ru.point.fasting.domain.ChangeStartIntervalTimeUseCase
import ru.point.fasting.domain.GetScenarioUseCase
import ru.point.fasting.domain.UpdateFastingBackendInfoUseCase
import ru.point.fasting.domain.GetTimerUseCase
import ru.point.fasting.domain.Scenario
import ru.point.fasting.domain.StartTimerUseCase
import ru.point.fasting.domain.StopTimerUseCase
import ru.point.fasting.domain.TogglePhaseUseCase
import ru.point.fasting.domain.UpdateScenarioUseCase
import ru.point.fasting.domain.UserTimer

class FastingViewModel(
    private val getTimerUseCase: GetTimerUseCase,
    private val getScenarioUseCase: GetScenarioUseCase,
    private val updateFastingBackendInfoUseCase: UpdateFastingBackendInfoUseCase,
    private val updateScenarioUseCase: UpdateScenarioUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val stopTimerUseCase: StopTimerUseCase,
    private val adjustStartUseCase: AdjustStartUseCase,
    private val togglePhaseUseCase: TogglePhaseUseCase,
    private val changeStartIntervalTimeUseCase: ChangeStartIntervalTimeUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<UserTimer?>(null)
    val uiState: StateFlow<UserTimer?> = _uiState.asStateFlow()

    private val _updateScenarioResult = MutableSharedFlow<Boolean>()   // true=success, false=error
    val updateScenarioResult: SharedFlow<Boolean> = _updateScenarioResult

    private val _scenarioUiState = MutableStateFlow<Scenario?>(null)
    val scenarioUiState: StateFlow<Scenario?> = _scenarioUiState.asStateFlow()

    private suspend fun syncWithBackend(): UpdateFastingBackendInfoResult {
        val timer = _uiState.value ?: return UpdateFastingBackendInfoResult.Error("NoState")
        return updateFastingBackendInfoUseCase(
            timer.userFastingId,
            timer.userId,
            timer.status.name,
            timer.startTime,
            timer.endTime,
            timer.eatingWhileFasting,
            timer.isActive,
            timer.lastUpdate
        )
    }

    fun loadScenario(scenarioId: String){
        viewModelScope.launch {
            val scenario = getScenarioUseCase(scenarioId)
            _scenarioUiState.value = scenario
        }
    }

//    fun getUpdatedTimer(userId: String){
//        viewModelScope.launch {
//            getTimerUseCase(userId)
//                .collect { timer ->
//                    _uiState.value = timer
//                    if (timer != null) syncWithBackend()
//                }
//        }
//    }

    fun loadTimer(userId: String) {
        viewModelScope.launch {

            getTimerUseCase.initialFetch(userId)

            getTimerUseCase(userId)
                .collect { timer ->
                    _uiState.value = timer
                    if (timer != null) syncWithBackend()
                }
        }
    }

    fun changeStartInterval(epochMillis: Long, userId: String){
        viewModelScope.launch {

            _uiState.value?.let { timer ->
                changeStartIntervalTimeUseCase(timer.userId, epochMillis)
            }

            getTimerUseCase(userId)
                .collect { timer ->
                    _uiState.value = timer
                    if (timer != null) syncWithBackend()
                }
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
                if (timer != null) syncWithBackend()
            }
    }

    fun onStop(userId: String) = viewModelScope.launch {
        stopTimerUseCase(userId)

        getTimerUseCase(userId)
            .collect { timer ->
                _uiState.value = timer
                if (timer != null) syncWithBackend()
            }
    }

    fun onAdjustStart(newStart: Long, userId: String) = viewModelScope.launch {
        _uiState.value?.let { timer ->
            adjustStartUseCase(timer.userId, newStart)
        }

        getTimerUseCase(userId)
            .collect { timer ->
                _uiState.value = timer
                if (timer != null) syncWithBackend()
            }
    }

    fun updateScenario(userId: String, scenarioId: String) {
        viewModelScope.launch {
            stopTimerUseCase(userId)

            when (updateScenarioUseCase(userId, scenarioId)) {
                is UpdateFastingBackendInfoResult.Success -> {
                    _updateScenarioResult.emit(true)
                    loadTimer(userId)
                }
                is UpdateFastingBackendInfoResult.Error -> {
                    _updateScenarioResult.emit(false)
                }
            }
        }
    }



}