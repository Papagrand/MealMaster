package ru.point.api.registration.domain

sealed class RegistrationCheckResult {
    object Available : RegistrationCheckResult()
    data class Occupied(val message: String) : RegistrationCheckResult()
    data class Error(val message: String) : RegistrationCheckResult()
}
