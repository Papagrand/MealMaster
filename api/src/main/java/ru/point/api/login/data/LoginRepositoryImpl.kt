package ru.point.api.login.data

import ru.point.api.login.domain.LoginCheckResult
import ru.point.api.login.domain.LoginRepository
import ru.point.api.login.domain.LoginResult

class LoginRepositoryImpl (
    private val loginService: LoginService
) : LoginRepository {
    override suspend fun checkLoginAuth(login: String): LoginCheckResult {
        return try {
            val response = loginService.checkLoginAuth(login)
            when (response.code()) {
                200 -> {
                    if (response.body()?.success == true) {
                        LoginCheckResult.UserExists
                    } else {
                        // unusual scenario
                        LoginCheckResult.Error("User not found (body)")
                    }
                }
                403 -> {
                    // user not found
                    LoginCheckResult.Error("Пользователь не существует")
                }
                else -> {
                    val msg = response.errorBody()?.string() ?: "Unexpected error"
                    LoginCheckResult.Error(msg)
                }
            }
        } catch (e: Exception) {
            LoginCheckResult.Error("Ошибка проверки логина: ${e.localizedMessage}")
        }
    }

    override suspend fun checkConnection(): LoginResult {
        return try {
            val response = loginService.checkConnection()
            if (response.isSuccessful && response.body()?.success == true) {
                LoginResult.Success
            } else {
                LoginResult.Failure("Проверка соединения неуспешна")
            }
        } catch (e: Exception) {
            LoginResult.Failure("Нет соединения: ${e.localizedMessage}")
        }
    }

    override suspend fun checkProfileExist(login: String): Boolean {
        return try {
            val response = loginService.checkProfileExist(login)
            when (response.code()) {
                200 -> true
                403 -> false
                else -> false
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun loginUser(login: String, password: String, deviceId: String): LoginResult {
        return try {
            val resp: LoginResponse = loginService.login(LoginRequest(login, password, deviceId))
            if (resp.success) {
                LoginResult.Success
            } else {
                LoginResult.Failure(resp.message ?: "Неверно введен пароль")
            }
        } catch (e: Exception) {
            LoginResult.Failure("Ошибка авторизации: ${e.localizedMessage}")
        }
    }
}