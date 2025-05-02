package ru.point.fasting.data

import kotlinx.coroutines.flow.Flow
import ru.point.fasting.domain.Scenario
import ru.point.fasting.domain.TimerStatus
import ru.point.fasting.domain.UserTimer

interface TimerRepository {

    fun observeTimer(userId: String): Flow<UserTimer?>

    fun observeScenario(): Flow<Scenario?>

    suspend fun fetchAndCache(userId: String)

    suspend fun getCurrentTimer(userId: String): UserTimer?

    suspend fun updateTimer(
        userId: String,
        status: TimerStatus,
        startMillis: Long,
        endMillis: Long
    )

    suspend fun updateStatusOff(userId: String)

    suspend fun togglePhase(userId: String)

    suspend fun adjustTimerWindow(
        userId: String,
        newPhase: TimerStatus,
        newStartMillis: Long,
        newEndMillis: Long
    )
}