package ru.point.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import ru.point.api.registration.RegistrationRequest
import ru.point.api.registration.RegistrationResponse
import ru.point.api.registration.RegistrationService
import ru.point.auth.ui.register.validators.EmailValidator
import ru.point.auth.ui.register.validators.LoginValidator
import javax.inject.Inject
import javax.inject.Provider


const val timeoutMillis: Long = 2000


data class RegisterUiState(
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null,
    val isRegistered: Boolean = false,
    val emailAvailable: Boolean = false,
    val loginAvailable: Boolean = false,
    val passwordAvailable: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isRepeatPasswordVisible: Boolean = false
)

sealed class RegisterUiEvent {
    object NavigateToLogin : RegisterUiEvent()
    data class ShowToast(val message: String) : RegisterUiEvent()
}

class RegistrationViewModel(
    private val registrationService: RegistrationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // SharedFlow для одноразовых событий (навигация, уведомления и т.д.).
    private val _uiEvent = MutableSharedFlow<RegisterUiEvent>()
    val uiEvent: SharedFlow<RegisterUiEvent> = _uiEvent.asSharedFlow()

    private val _emailInput = MutableStateFlow("")

    // Flow для отслеживания ввода логина. Здесь применён debounce.
    private val _loginInput = MutableStateFlow("")

    // Flow для ввода пароля (если потребуется валидация на лету).
    private val _passwordInput = MutableStateFlow("")
    private val _repeatPasswordInput = MutableStateFlow("")


    init {
        observeEmailInput()
        observeLoginInput()
    }

    private fun observeLoginInput() {
        viewModelScope.launch {
            _loginInput
                .debounce(ru.point.auth.ui.login.timeoutMillis) // Ждем 2 секунды бездействия пользователя
                .distinctUntilChanged()
                .collectLatest { login ->
                    when {
                        login.isEmpty()-> {
                            _uiState.update {
                                it.copy(
                                    loginError = null,
                                    loginAvailable = false
                                )
                            }
                        }

                        login.isNotEmpty() && !LoginValidator.isValid(login) -> {
                            _uiState.update {
                                it.copy(
                                    loginError = "Введите корректный Логин",
                                    loginAvailable = false
                                )
                            }
                        }

                        else -> {
                            // Если локально формат корректный, очищаем ошибку перед удалённой проверкой
                            _uiState.update { it.copy(loginError = null) }
                            checkLogin(login)
                        }
                    }
                }
        }
    }

    private fun observeEmailInput() {
        viewModelScope.launch {
            _emailInput
                .debounce(timeoutMillis)
                .distinctUntilChanged()
                .collectLatest { email ->
                    when {

                        email.isEmpty() -> {
                            _uiState.update {
                                it.copy(
                                    emailError = null,
                                    emailAvailable = false
                                )
                            }
                        }

                        email.isNotEmpty() && !EmailValidator.isValid(email) -> {
                            _uiState.update {
                                it.copy(
                                    emailError = "Введите корректный Email",
                                    emailAvailable = false
                                )
                            }
                        }

                        else -> {
                            // Если локально формат корректный, очищаем ошибку перед удалённой проверкой
                            _uiState.update { it.copy(emailError = null) }
                            checkEmail(email)
                        }
                    }
                }
        }
    }


    fun onEmailChanged(email: String) {
        _emailInput.value = email.trim()
    }

    fun onLoginChanged(login: String) {
        _loginInput.value = login.trim()
    }

    fun onPasswordChanged(password: String) {
        _passwordInput.value = password
        // Выполняем локальную валидацию пароля
        if (!PasswordValidator.isLongEnough(password)) {
            _uiState.update { it.copy(passwordError = "Пароль должен быть не менее 8 символов") }
        } else if (!PasswordValidator.containsOnlyAllowedCharacters(password)) {
            _uiState.update { it.copy(passwordError = "Пароль содержит недопустимые символы") }
        } else {
            _uiState.update { it.copy(passwordError = null) }
        }
        // Перепроверяем совпадение с повторным паролем
        if (_repeatPasswordInput.value.isNotEmpty()){
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


    private suspend fun checkEmail(email: String) {
        _uiState.update { it.copy(isLoading = true, emailError = null) }
        try {
            val response = registrationService.checkEmail(email)
            when (response.code()) {
                404 -> {
                    // 404 означает, что email не найден – email доступен
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            emailError = null,
                            emailAvailable = true
                        )
                    }
                }

                302 -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false, emailError = "Email занят", emailAvailable = false
                        )
                    }
                }

                else -> {
                    val errorMsg = response.errorBody()?.string() ?: "Unexpected error"
                    _uiState.update {
                        it.copy(isLoading = false, emailError = errorMsg, emailAvailable = false)
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    emailError = "Ошибка проверки Email",
                    emailAvailable = false
                )
            }
        }
    }


    private suspend fun checkLogin(login: String) {
        _uiState.update { it.copy(isLoading = true, loginError = null) }
        try {
            val response = registrationService.checkLogin(login)
            when (response.code()) {
                404 -> {
                    // 404 означает, что email не найден – email доступен
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = null,
                            loginAvailable = true
                        )
                    }
                }

                302 -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false, loginError = "Логин занят", loginAvailable = false
                        )
                    }
                }

                else -> {
                    val errorMsg = response.errorBody()?.string() ?: "Unexpected error"
                    _uiState.update {
                        it.copy(isLoading = false, loginError = errorMsg, loginAvailable = false)
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    loginError = "Ошибка проверки Логина",
                    emailAvailable = false
                )
            }
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleRepeatPasswordVisibility() {
        _uiState.update { it.copy(isRepeatPasswordVisible = !it.isRepeatPasswordVisible) }
    }


    fun register() {
        val email = _emailInput.value
        val login = _loginInput.value
        val password = _passwordInput.value
        val repeatPassword = _repeatPasswordInput.value

        var isValid = true

        // Локальная проверка email
        if (email.isEmpty() || !EmailValidator.isValid(email)) {
            _uiState.update { it.copy(emailError = "Введите корректный Email") }
            isValid = false
        } else {
            _uiState.update { it.copy(emailError = null) }
            // Только если email локально валиден, проверяем доступность
            if (!_uiState.value.emailAvailable) {
                _uiState.update { it.copy(emailError = "Email занят") }
                isValid = false
            }
        }

        // Локальная проверка логина
        if (login.isEmpty() || !LoginValidator.isValid(login)) {
            _uiState.update { it.copy(loginError = "Введите корректный логин") }
            isValid = false
        } else {
            _uiState.update { it.copy(loginError = null) }
            // Только если логин локально валиден, проверяем его доступность
            if (!_uiState.value.loginAvailable) {
                _uiState.update { it.copy(loginError = "Логин занят") }
                isValid = false
            }
        }

        // Локальная проверка пароля
        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "Введите пароль") }
            isValid = false
        } else if (!PasswordValidator.isLongEnough(password)) {
            _uiState.update { it.copy(passwordError = "Пароль должен быть не менее 8 символов") }
            isValid = false
        } else if (!PasswordValidator.containsOnlyAllowedCharacters(password)) {
            _uiState.update { it.copy(passwordError = "Пароль содержит недопустимые символы") }
            isValid = false
        } else {
            _uiState.update { it.copy(passwordError = null) }
        }

        // Проверка совпадения повторного пароля с основным
        if (repeatPassword != password) {
            _uiState.update { it.copy(repeatPasswordError = "Пароли не совпадают") }
            isValid = false
        } else {
            _uiState.update { it.copy(repeatPasswordError = null) }
        }

        if (!isValid) return

        // Если все проверки пройдены – запускаем запрос регистрации
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val request = RegistrationRequest(login = login, email = email, password = password)
                val response: RegistrationResponse = registrationService.register(request)
                if (response.success) {
                    _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                    _uiEvent.emit(RegisterUiEvent.ShowToast("Регистрация прошла успешно"))
                    _uiEvent.emit(RegisterUiEvent.NavigateToLogin)
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(
                        RegisterUiEvent.ShowToast(
                            response.message ?: "Ошибка регистрации"
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.emit(RegisterUiEvent.ShowToast("Ошибка регистрации: ${e.message}"))
            }
        }
    }


    class Factory @Inject constructor(
        private val registrationService: Provider<RegistrationService>
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == RegistrationViewModel::class.java)
            return RegistrationViewModel(registrationService.get()) as T
        }
    }
}
