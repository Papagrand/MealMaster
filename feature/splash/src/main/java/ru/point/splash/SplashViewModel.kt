package ru.point.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.login.data.createLoginService
import ru.point.api.login.data.LoginService
import ru.point.core.secure_prefs.SecurePrefs

sealed class SplashUiEvent {
    object NavigateToLogin : SplashUiEvent()
    object NavigateToCreateProfile : SplashUiEvent()
    object NavigateToMain : SplashUiEvent()
}

data class SplashUiState(
    val deviceId: String = ""
)

class SplashViewModel(
    private val loginService: LoginService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SplashUiEvent>()
    val uiEvent: SharedFlow<SplashUiEvent> = _uiEvent

    fun updateDeviceId(deviceId: String){
        _uiState.value = _uiState.value.copy(deviceId = deviceId)
        checkUserProfile(_uiState.value.deviceId)
    }

    private fun checkUserProfile(deviceId: String) {
        viewModelScope.launch {
            // Получаем userId из SecurePrefs
            val userId = SecurePrefs.getUserId()
            if (userId.isNullOrEmpty()) {
                _uiEvent.emit(SplashUiEvent.NavigateToLogin)
            } else {
                try {
                    // Сначала проверяем авторизацию пользователя
                    val authResponse = loginService.checkAuthUser(userId, deviceId)
                    if (authResponse.isSuccessful) {
                        val authBody = authResponse.body()
                        // Если available == true – пользователь авторизован, продолжаем проверку профиля
                        if (authBody?.success == true) {
                            // Проверяем, существует ли профиль, используя код ответа
                            val profileResponse = loginService.checkProfileExist(userId)
                            when (profileResponse.code()) {
                                200 -> _uiEvent.emit(SplashUiEvent.NavigateToMain)
                                403 -> _uiEvent.emit(SplashUiEvent.NavigateToCreateProfile)
                                else -> _uiEvent.emit(SplashUiEvent.NavigateToLogin)
                            }
                        } else {
                            // Если авторизация не пройдена (success == false)
                            _uiEvent.emit(SplashUiEvent.NavigateToLogin)
                        }
                    } else {
                        _uiEvent.emit(SplashUiEvent.NavigateToLogin)
                    }
                } catch (_: Exception) {
                    // При исключении (например, проблемах с сетью) – перенаправляем на логин
                    _uiEvent.emit(SplashUiEvent.NavigateToLogin)
                }
            }
        }
    }

}

class SplashViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SplashViewModel::class.java)
        // Создаем экземпляр LoginService через createLoginService
        val loginService = createLoginService()
        return SplashViewModel(loginService) as T
    }
}