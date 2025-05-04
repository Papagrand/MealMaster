package ru.point.api.profile_data.domain

sealed class LogoutUserResult{
    object Success : LogoutUserResult()
    data class Failure(val message: String) : LogoutUserResult()
}