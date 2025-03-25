package ru.point.auth.ui.register.domain

import ru.point.api.registration.domain.RegistrationRepository
import ru.point.api.registration.domain.RegistrationResult

class RegisterUserUseCase(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(login: String, email: String, password: String): RegistrationResult {
        return repository.registerUser(login, email, password)
    }
}