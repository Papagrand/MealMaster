package ru.point.api.timer_fasting.domain

sealed class UpdateFastingBackendInfoResult {
    object Success : UpdateFastingBackendInfoResult()
    data class Error(val message: String) : UpdateFastingBackendInfoResult()
}