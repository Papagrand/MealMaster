package ru.point.api.profile_data.domain

sealed class UpdateProfileResult {
    object Success : UpdateProfileResult()
    data class Error(val message: String) : UpdateProfileResult()
}