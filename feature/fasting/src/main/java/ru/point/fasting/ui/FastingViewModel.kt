package ru.point.fasting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.fasting.domain.AdjustStartUseCase
import ru.point.fasting.domain.GetTimerUseCase
import ru.point.fasting.domain.StartTimerUseCase
import ru.point.fasting.domain.StopTimerUseCase
import ru.point.fasting.domain.UserTimer

class FastingViewModel(
    private val getTimerUseCase: GetTimerUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val stopTimerUseCase: StopTimerUseCase,
    private val adjustStartUseCase: AdjustStartUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<UserTimer?>(null)
    val uiState: StateFlow<UserTimer?> = _uiState.asStateFlow()


    fun loadTimer(userId: String) {
        viewModelScope.launch {
            getTimerUseCase(userId)
                .collect { timer ->
                    _uiState.value = timer
                }
        }
    }

    fun onToggle() = viewModelScope.launch {
        _uiState.value?.let { timer ->
            if (timer.isActive) stopTimerUseCase(timer.userId)
            else startTimerUseCase(timer.userId)
        }
    }

    fun onAdjustStart(newStart: Long) = viewModelScope.launch {
        _uiState.value?.let { timer ->
            adjustStartUseCase(timer.userId, newStart)
        }
    }

}