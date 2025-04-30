package ru.point.fasting.data

import kotlinx.coroutines.flow.Flow
import ru.point.fasting.domain.UserTimer

interface TimerRepository {

    fun observeTimer(userId: String): Flow<UserTimer?>

    suspend fun startTimer(userId: String)

    suspend fun stopTimer(userId: String)

    suspend fun adjustStart(userId: String, newStartMillis: Long)

}