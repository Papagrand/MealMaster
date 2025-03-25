package ru.point.api.registration.domain

sealed class RegistrationResult {
    object Success : RegistrationResult()
    data class Failure(val message: String) : RegistrationResult()
}