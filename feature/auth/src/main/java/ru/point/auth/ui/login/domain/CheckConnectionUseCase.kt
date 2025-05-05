package ru.point.auth.ui.login.domain

import ru.point.api.login.domain.LoginCheckResult
import ru.point.api.login.domain.LoginRepository
import ru.point.api.login.domain.LoginResult

class CheckConnectionUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(): LoginResult {
        return repository.checkConnection()
    }
}