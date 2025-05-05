package ru.point.api.login.domain

interface LoginRepository {

    suspend fun checkLoginAuth(login: String): LoginCheckResult

    suspend fun checkConnection(): LoginResult

    suspend fun checkProfileExist(login: String): Boolean

    suspend fun loginUser(login: String, password: String, deviceId: String): LoginResult
}