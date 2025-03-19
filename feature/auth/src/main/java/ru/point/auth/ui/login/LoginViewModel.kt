package ru.point.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.point.api.login.LoginCheckResponseAuth
import ru.point.api.login.LoginRequest
import ru.point.api.login.LoginResponse
import ru.point.api.login.LoginService
import ru.point.api.login.ProfileExistCheckResponse
import ru.point.auth.ui.register.RegistrationViewModel
import ru.point.core.secure_prefs.SecurePrefs
import javax.inject.Inject
import javax.inject.Provider


const val timeoutMillis: Long = 2000

data class LoginUiState(
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val passwordError: String? = null,
    val isAuthorized: Boolean = false,
    // Флаг, указывающий, что введённый логин найден (доступен)
    val loginAvailable: Boolean = false,
    val profileExist: Boolean = false
)


sealed class LoginUiEvent {
    object NavigateToOnboarding : LoginUiEvent()
    object NavigateToHomeProgress : LoginUiEvent()
    data class ShowToast(val message: String) : LoginUiEvent()
}

class LoginViewModel(
    private val loginService: LoginService
) : ViewModel() {

    // StateFlow для хранения текущего состояния UI.
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // SharedFlow для одноразовых событий (навигация, уведомления и т.д.).
    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    // Flow для отслеживания ввода логина. Здесь применён debounce.
    private val _loginInput = MutableStateFlow("")
    // Flow для ввода пароля (если потребуется валидация на лету).
    private val _passwordInput = MutableStateFlow("")


    init {
        observeLoginInput()
    }

    private fun observeLoginInput() {
        viewModelScope.launch {
            _loginInput
                .debounce(timeoutMillis) // Ждем 2 секунды бездействия пользователя
                .distinctUntilChanged()
                .collectLatest { login ->
                    if (login.isNotEmpty()) {
                        checkLogin(login)
                    } else {
                        // Если строка пуста – очищаем ошибку
                        _uiState.update { it.copy(loginError = null, loginAvailable = false) }
                    }
                }
        }
    }




    fun onLoginChanged(login: String) {
        _loginInput.value = login.trim()
    }

    fun onPasswordChanged(password: String) {
        _passwordInput.value = password
    }


    private suspend fun checkLogin(login: String) {
        _uiState.update { it.copy(isLoading = true, loginError = null) }
        try {
            val response = loginService.checkLoginAuth(login)
            when (response.code()) {
                404 -> {
                    // Пользователь не найден
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = "Пользователь не существует",
                            loginAvailable = false
                        )
                    }
                }
                302 -> {
                    // Пользователь найден – логин доступен
                    _uiState.update {
                        it.copy(isLoading = false, loginError = null, loginAvailable = true)
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
                it.copy(isLoading = false, loginError = "Ошибка проверки Login", loginAvailable = false)
            }
        }
    }

    private suspend fun checkExistProfile(login: String){
        try {
            val response = loginService.checkProfileExist(login)
            when(response.code()){
                302 ->{
                    _uiState.update {
                        it.copy(profileExist = true)
                    }
                }
                404 -> {
                    _uiState.update {
                        it.copy(profileExist = false)
                    }
                }
                else ->{
                    _uiState.update {
                        it.copy(profileExist = false)
                    }
                }
            }
        }catch (e: Exception) {
            _uiState.update {
                it.copy(profileExist = false)
            }
        }
    }

    fun login(deviceId: String) {
        // Берем актуальные значения логина и пароля
        val login = _loginInput.value
        val password = _passwordInput.value

        // Валидация ввода
        if (login.isEmpty()) {
            _uiState.update { it.copy(loginError = "Введите верный логин") }
            return
        }
        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "Неверно введен пароль") }
            return
        }
        if (!_uiState.value.loginAvailable) {
            _uiState.update { it.copy(loginError = "Пользователь с данным логином не существует") }
            return
        }

        // Обновляем состояние – начинаем загрузку
        _uiState.update { it.copy(isLoading = true, passwordError = null) }

        viewModelScope.launch {
            try {
                val response: LoginResponse = loginService.login(LoginRequest(login, password, deviceId))
                if (response.success) {
                    // Авторизация успешна – обновляем состояние и отправляем события
                    _uiState.update { it.copy(isLoading = false, isAuthorized = true) }
                    _uiEvent.emit(LoginUiEvent.ShowToast("Вы авторизованы"))

                    SecurePrefs.saveUserId(login)

                    checkExistProfile(login)

                    if (_uiState.value.profileExist){
                        _uiEvent.emit(LoginUiEvent.NavigateToHomeProgress)
                    }else{
                        _uiEvent.emit(LoginUiEvent.NavigateToOnboarding)
                    }
                } else {
                    // Ошибка авторизации – неверный пароль
                    _uiState.update {
                        it.copy(isLoading = false, passwordError = "Неверно введен пароль")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, passwordError = "Ошибка авторизации")
                }
            }
        }
    }


    class Factory @Inject constructor(
        private val loginService: Provider<LoginService>
    ) : ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == LoginViewModel::class.java)
            return LoginViewModel(loginService.get()) as T
        }
    }
}