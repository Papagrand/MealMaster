package ru.point.api.login.domain

sealed class LoginResult {
    object Success : LoginResult()
    data class Failure(val message: String) : LoginResult()
}