package ru.point.api.registration.domain

interface RegistrationRepository {

    suspend fun checkEmail(email: String): RegistrationCheckResult

    suspend fun checkLogin(login: String): RegistrationCheckResult

    suspend fun registerUser(login: String, email: String, password: String): RegistrationResult

}