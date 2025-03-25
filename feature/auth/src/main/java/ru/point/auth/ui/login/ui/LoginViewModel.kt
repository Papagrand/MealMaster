package ru.point.auth.ui.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.point.api.login.domain.LoginCheckResult
import ru.point.api.login.domain.LoginResult
import ru.point.auth.ui.login.domain.CheckLoginAuthUseCase
import ru.point.auth.ui.login.domain.CheckProfileExistUseCase
import ru.point.auth.ui.login.domain.LoginUserUseCase
import ru.point.core.secure_prefs.SecurePrefs


private const val timeoutMillis: Long = 2000

data class LoginUiState(
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val passwordError: String? = null,
    val isAuthorized: Boolean = false,
    val loginAvailable: Boolean = false,
    val profileExist: Boolean = false
)

sealed class LoginUiEvent {
    data object NavigateToOnboarding : LoginUiEvent()
    data object NavigateToHomeProgress : LoginUiEvent()
    data class ShowToast(val message: String) : LoginUiEvent()
}

class LoginViewModel(
    private val checkLoginAuthUseCase: CheckLoginAuthUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val checkProfileExistUseCase: CheckProfileExistUseCase
) : ViewModel() {

//    private val uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState>
        field = MutableStateFlow(LoginUiState())

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    private val _loginInput = MutableStateFlow("")
    private val _passwordInput = MutableStateFlow("")

    init {
        observeLoginInput()
    }

    private fun observeLoginInput() {
        viewModelScope.launch {
            _loginInput
                .debounce(timeoutMillis)
                .distinctUntilChanged()
                .collectLatest { login ->
                    if (login.isEmpty()) {
                        uiState.update {
                            it.copy(loginError = null, loginAvailable = false)
                        }
                    } else {
                        checkLoginRemote(login)
                    }
                }
        }
    }

    private suspend fun checkLoginRemote(login: String) {
        uiState.update { it.copy(isLoading = true, loginError = null) }
        val result = checkLoginAuthUseCase(login)
        uiState.update { it.copy(isLoading = false) }

        when (result) {
            is LoginCheckResult.UserExists -> {
                // Пользователь существует
                uiState.update { it.copy(loginError = null, loginAvailable = true) }
            }
            is LoginCheckResult.Error -> {
                uiState.update { it.copy(loginError = result.message, loginAvailable = false) }
            }
        }
    }

    suspend fun checkProfileExist(login: String) {
        val exist = checkProfileExistUseCase(login)
        uiState.update { it.copy(profileExist = exist) }
    }

    fun onLoginChanged(login: String) {
        _loginInput.value = login.trim()
    }

    fun onPasswordChanged(password: String) {
        _passwordInput.value = password
    }

    fun login(deviceId: String) {
        val login = _loginInput.value
        val password = _passwordInput.value

        // Локальная проверка
        if (login.isEmpty()) {
            uiState.update { it.copy(loginError = "Введите верный логин") }
            return
        }
        if (password.isEmpty()) {
            uiState.update { it.copy(passwordError = "Неверно введен пароль") }
            return
        }
        if (!uiState.value.loginAvailable) {
            uiState.update { it.copy(loginError = "Пользователь с данным логином не существует") }
            return
        }

        uiState.update { it.copy(isLoading = true, passwordError = null) }

        viewModelScope.launch {
            val result = loginUserUseCase(login, password, deviceId)
            uiState.update { it.copy(isLoading = false) }

            when (result) {
                is LoginResult.Success -> {
                    _uiEvent.emit(LoginUiEvent.ShowToast("Вы авторизованы"))
                    SecurePrefs.saveUserId(login)

                    // Проверяем профиль
                    checkProfileExist(login)
                    if (uiState.value.profileExist) {
                        _uiEvent.emit(LoginUiEvent.NavigateToHomeProgress)
                    } else {
                        _uiEvent.emit(LoginUiEvent.NavigateToOnboarding)
                    }
                }
                is LoginResult.Failure -> {
                    uiState.update { it.copy(passwordError = result.message) }
                }
            }
        }
    }
}