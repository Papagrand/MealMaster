package ru.point.auth.ui.register.domain

import ru.point.api.registration.domain.RegistrationCheckResult
import ru.point.api.registration.domain.RegistrationRepository

class CheckEmailUseCase(
    private val registrationRepository: RegistrationRepository
) {
    suspend operator fun invoke(email: String): RegistrationCheckResult {
        return registrationRepository.checkEmail(email)
    }
}