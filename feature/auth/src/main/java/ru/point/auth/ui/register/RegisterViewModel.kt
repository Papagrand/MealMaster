package ru.point.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.point.api.registration.EmailCheckResponse
import ru.point.api.registration.LoginCheckResponse
import ru.point.api.registration.RegistrationRequest
import ru.point.api.registration.RegistrationResponse
import ru.point.api.registration.RegistrationService
import javax.inject.Inject
import javax.inject.Provider

class RegistrationViewModel(
    private val registrationService: RegistrationService
) : ViewModel() {

    fun register(login: String, email: String, password: String): Flow<RegistrationResponse> = flow {
        val response = registrationService.register(RegistrationRequest(login, email, password))
        emit(response) // Эмитим результат запроса
    }.flowOn(Dispatchers.IO)
        .catch { e ->
            // Если возникла ошибка сети или другая проблема, выбрасываем её дальше,
            // чтобы фрагмент мог обработать и показать сообщение об ошибке.
            throw e
        }



    fun checkEmail(email: String): Flow<EmailCheckResponse> = flow {
        val response = registrationService.checkEmail(email)
        when (response.code()) {
            404 -> {
                // 404 означает, что email не найден – email доступен
                emit(EmailCheckResponse(available = true, message = "Email is available"))
            }
            302 -> {
                // 302 означает, что email найден – email уже занят
                // Если сервер возвращает текст ошибки в теле, можно его извлечь, иначе использовать стандартное сообщение
                val errorMsg = response.errorBody()?.string() ?: "Email already taken"
                emit(EmailCheckResponse(available = false, message = errorMsg))
            }
            else -> {
                val errorMsg = response.errorBody()?.string() ?: "Unexpected error"
                emit(EmailCheckResponse(available = false, message = errorMsg))
            }
        }
    }.flowOn(Dispatchers.IO)
        .catch { e ->
            e.printStackTrace()
            throw e
        }


    fun checkLogin(login: String): Flow<LoginCheckResponse> = flow {
        val response = registrationService.checkLogin(login)
        when (response.code()) {
            404 -> {
                emit(LoginCheckResponse(available = true, message = "Login is available"))
            }
            302 -> {
                val errorMsg = response.errorBody()?.string() ?: "Email already taken"
                emit(LoginCheckResponse(available = false, message = errorMsg))
            }
            else -> {
                val errorMsg = response.errorBody()?.string() ?: "Unexpected error"
                emit(LoginCheckResponse(available = false, message = errorMsg))
            }
        }
    }.flowOn(Dispatchers.IO)
        .catch { e ->
            e.printStackTrace()
            throw e
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
