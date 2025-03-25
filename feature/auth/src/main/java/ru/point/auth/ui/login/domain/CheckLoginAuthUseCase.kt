package ru.point.auth.ui.login.domain

import ru.point.api.login.domain.LoginCheckResult
import ru.point.api.login.domain.LoginRepository

class CheckLoginAuthUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(login: String): LoginCheckResult {
        return repository.checkLoginAuth(login)
    }
}