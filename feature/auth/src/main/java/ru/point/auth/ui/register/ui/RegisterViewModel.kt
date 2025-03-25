package ru.point.auth.ui.register.ui

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
import ru.point.api.registration.domain.RegistrationCheckResult
import ru.point.api.registration.domain.RegistrationResult
import ru.point.auth.ui.register.PasswordValidator
import ru.point.auth.ui.register.domain.CheckEmailUseCase
import ru.point.auth.ui.register.domain.CheckLoginUseCase
import ru.point.auth.ui.register.domain.RegisterUserUseCase
import ru.point.auth.ui.register.validators.EmailValidator
import ru.point.auth.ui.register.validators.LoginValidator


private const val TIMEOUT_MILLIS: Long = 2000L

data class RegisterUiState(
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null,
    val isRegistered: Boolean = false,
    val emailAvailable: Boolean = false,
    val loginAvailable: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isRepeatPasswordVisible: Boolean = false
)

sealed class RegisterUiEvent {
    object NavigateToLogin : RegisterUiEvent()
    data class ShowToast(val message: String) : RegisterUiEvent()
}

class RegistrationViewModel(
    private val checkEmailUseCase: CheckEmailUseCase,
    private val checkLoginUseCase: CheckLoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RegisterUiEvent>()
    val uiEvent: SharedFlow<RegisterUiEvent> = _uiEvent.asSharedFlow()

    // Поля, хранящие ввод:
    private val _emailInput = MutableStateFlow("")
    private val _loginInput = MutableStateFlow("")
    private val _passwordInput = MutableStateFlow("")
    private val _repeatPasswordInput = MutableStateFlow("")

    init {
        observeEmailInput()
        observeLoginInput()
    }

    private fun observeEmailInput() {
        viewModelScope.launch {
            _emailInput
                .debounce(TIMEOUT_MILLIS)
                .distinctUntilChanged()
                .collectLatest { email ->
                    when {
                        email.isEmpty() -> {
                            _uiState.update {
                                it.copy(emailError = null, emailAvailable = false)
                            }
                        }
                        !EmailValidator.isValid(email) -> {
                            _uiState.update {
                                it.copy(emailError = "Введите корректный Email", emailAvailable = false)
                            }
                        }
                        else -> {
                            _uiState.update { it.copy(emailError = null) }
                            checkEmailRemote(email)
                        }
                    }
                }
        }
    }

    private suspend fun checkEmailRemote(email: String) {
        _uiState.update { it.copy(isLoading = true) }
        val result = checkEmailUseCase(email)
        _uiState.update { it.copy(isLoading = false) }

        when (result) {
            is RegistrationCheckResult.Available -> {
                _uiState.update {
                    it.copy(emailError = null, emailAvailable = true)
                }
            }
            is RegistrationCheckResult.Occupied -> {
                _uiState.update {
                    it.copy(emailError = result.message, emailAvailable = false)
                }
            }
            is RegistrationCheckResult.Error -> {
                _uiState.update {
                    it.copy(emailError = result.message, emailAvailable = false)
                }
            }
        }
    }

    private fun observeLoginInput() {
        viewModelScope.launch {
            _loginInput
                .debounce(TIMEOUT_MILLIS)
                .distinctUntilChanged()
                .collectLatest { login ->
                    when {
                        login.isEmpty() -> {
                            _uiState.update {
                                it.copy(loginError = null, loginAvailable = false)
                            }
                        }
                        !LoginValidator.isValid(login) -> {
                            _uiState.update {
                                it.copy(loginError = "Введите корректный Логин", loginAvailable = false)
                            }
                        }
                        else -> {
                            _uiState.update { it.copy(loginError = null) }
                            checkLoginRemote(login)
                        }
                    }
                }
        }
    }

    private suspend fun checkLoginRemote(login: String) {
        _uiState.update { it.copy(isLoading = true) }
        val result = checkLoginUseCase(login)
        _uiState.update { it.copy(isLoading = false) }

        when (result) {
            is RegistrationCheckResult.Available -> {
                _uiState.update {
                    it.copy(loginError = null, loginAvailable = true)
                }
            }
            is RegistrationCheckResult.Occupied -> {
                _uiState.update {
                    it.copy(loginError = result.message, loginAvailable = false)
                }
            }
            is RegistrationCheckResult.Error -> {
                _uiState.update {
                    it.copy(loginError = result.message, loginAvailable = false)
                }
            }
        }
    }

    // --- Методы, которые вызывают эти flow'ы ---
    fun onEmailChanged(email: String) {
        _emailInput.value = email.trim()
    }

    fun onLoginChanged(login: String) {
        _loginInput.value = login.trim()
    }

    fun onPasswordChanged(password: String) {
        _passwordInput.value = password
        // Локальная проверка
        if (!PasswordValidator.isLongEnough(password)) {
            _uiState.update { it.copy(passwordError = "Пароль должен быть не менее 8 символов") }
        } else if (!PasswordValidator.containsOnlyAllowedCharacters(password)) {
            _uiState.update { it.copy(passwordError = "Пароль содержит недопустимые символы") }
        } else {
            _uiState.update { it.copy(passwordError = null) }
        }
        // Проверяем повторный пароль
        if (_repeatPasswordInput.value.isNotEmpty()) {
            validateRepeatPassword(_repeatPasswordInput.value, password)
        }
    }

    fun onRepeatPasswordChanged(repeatPassword: String) {
        _repeatPasswordInput.value = repeatPassword
        validateRepeatPassword(repeatPassword, _passwordInput.value)
    }

    private fun validateRepeatPassword(repeatPassword: String, password: String) {
        if (repeatPassword != password) {
            _uiState.update { it.copy(repeatPasswordError = "Пароли не совпадают") }
        } else {
            _uiState.update { it.copy(repeatPasswordError = null) }
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleRepeatPasswordVisibility() {
        _uiState.update { it.copy(isRepeatPasswordVisible = !it.isRepeatPasswordVisible) }
    }

    /**
     * Финальная регистрация
     */
    fun register() {
        val email = _emailInput.value
        val login = _loginInput.value
        val password = _passwordInput.value
        val repeatPassword = _repeatPasswordInput.value

        var isValid = true
        // Локальные проверки (можно вынести в отдельный UseCase, но оставим здесь)
        if (email.isBlank() || !EmailValidator.isValid(email)) {
            _uiState.update { it.copy(emailError = "Введите корректный Email") }
            isValid = false
        } else if (!_uiState.value.emailAvailable) {
            _uiState.update { it.copy(emailError = "Email занят") }
            isValid = false
        }
        if (login.isBlank() || !LoginValidator.isValid(login)) {
            _uiState.update { it.copy(loginError = "Введите корректный логин") }
            isValid = false
        } else if (!_uiState.value.loginAvailable) {
            _uiState.update { it.copy(loginError = "Логин занят") }
            isValid = false
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Введите пароль") }
            isValid = false
        }
        if (repeatPassword != password) {
            _uiState.update { it.copy(repeatPasswordError = "Пароли не совпадают") }
            isValid = false
        }

        if (!isValid) return

        // Запускаем корутину, вызываем UseCase
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = registerUserUseCase(login, email, password)
            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is RegistrationResult.Success -> {
                    _uiEvent.emit(RegisterUiEvent.ShowToast("Регистрация прошла успешно"))
                    _uiEvent.emit(RegisterUiEvent.NavigateToLogin)
                }
                is RegistrationResult.Failure -> {
                    _uiEvent.emit(RegisterUiEvent.ShowToast(result.message))
                }
            }
        }
    }
}
