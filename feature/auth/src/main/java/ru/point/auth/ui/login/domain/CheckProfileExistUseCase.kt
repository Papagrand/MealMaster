package ru.point.auth.ui.login.domain

import ru.point.api.login.domain.LoginRepository

class CheckProfileExistUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(login: String): Boolean {
        return repository.checkProfileExist(login)
    }
}