package ru.point.auth.ui.login.domain

import ru.point.api.login.domain.LoginRepository
import ru.point.api.login.domain.LoginResult

class LoginUserUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(login: String, password: String, deviceId: String): LoginResult {
        return repository.loginUser(login, password, deviceId)
    }
}