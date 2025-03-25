package ru.point.auth.ui.register.domain

import ru.point.api.registration.domain.RegistrationCheckResult
import ru.point.api.registration.domain.RegistrationRepository

class CheckLoginUseCase(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(login: String): RegistrationCheckResult {
        return repository.checkLogin(login)
    }
}