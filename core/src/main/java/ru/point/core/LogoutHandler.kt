package ru.point.core

interface LogoutHandler {
    suspend fun onLogout()
}