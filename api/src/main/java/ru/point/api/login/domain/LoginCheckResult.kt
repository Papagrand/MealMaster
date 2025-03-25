package ru.point.api.login.domain

sealed class LoginCheckResult {
    object UserExists : LoginCheckResult()
    data class Error(val message: String) : LoginCheckResult()
}