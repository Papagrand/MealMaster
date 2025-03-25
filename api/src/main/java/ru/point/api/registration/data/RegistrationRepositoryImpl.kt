package ru.point.api.registration.data

import ru.point.api.registration.domain.RegistrationCheckResult
import ru.point.api.registration.domain.RegistrationRepository
import ru.point.api.registration.domain.RegistrationResult

class RegistrationRepositoryImpl(
    private val service: RegistrationService
) : RegistrationRepository {

    override suspend fun checkEmail(email: String): RegistrationCheckResult {
        return try {
            val response = service.checkEmail(email)
            if (response.isSuccessful) {
                // Предположим, если success == true, значит Email свободен
                if (response.body()?.success == true) {
                    RegistrationCheckResult.Available
                } else {
                    RegistrationCheckResult.Occupied("Email занят")
                }
            } else {
                when (response.code()) {
                    403 -> RegistrationCheckResult.Occupied("Email занят")
                    else -> {
                        val msg = response.errorBody()?.string() ?: "Unknown error"
                        RegistrationCheckResult.Error(msg)
                    }
                }
            }
        } catch (e: Exception) {
            RegistrationCheckResult.Error("Нет соединения с сервером: ${e.localizedMessage}")
        }
    }

    override suspend fun checkLogin(login: String): RegistrationCheckResult {
        return try {
            val response = service.checkLogin(login)
            if (response.isSuccessful) {
                if (response.body()?.success == true) {
                    RegistrationCheckResult.Available
                } else {
                    RegistrationCheckResult.Occupied("Логин занят")
                }
            } else {
                when (response.code()) {
                    403 -> RegistrationCheckResult.Occupied("Логин занят")
                    else -> {
                        val msg = response.errorBody()?.string() ?: "Unknown error"
                        RegistrationCheckResult.Error(msg)
                    }
                }
            }
        } catch (e: Exception) {
            RegistrationCheckResult.Error("Нет соединения с сервером: ${e.localizedMessage}")
        }
    }

    override suspend fun registerUser(login: String, email: String, password: String): RegistrationResult {
        return try {
            val request = RegistrationRequest(login, email, password)
            val response = service.register(request)
            if (response.success) {
                RegistrationResult.Success
            } else {
                RegistrationResult.Failure(response.message ?: "Неизвестная ошибка регистрации")
            }
        } catch (e: Exception) {
            RegistrationResult.Failure("Нет соединения с сервером: ${e.localizedMessage}")
        }
    }
}