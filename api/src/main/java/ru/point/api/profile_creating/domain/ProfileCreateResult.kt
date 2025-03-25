package ru.point.api.profile_creating.domain

sealed class ProfileCreateResult {
    object Success : ProfileCreateResult()
    data class Error(val message: String) : ProfileCreateResult()
}