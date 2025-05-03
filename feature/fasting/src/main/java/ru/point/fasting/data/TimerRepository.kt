package ru.point.fasting.data

import kotlinx.coroutines.flow.Flow
import ru.point.api.timer_fasting.domain.UpdateFastingBackendInfoResult
import ru.point.fasting.domain.Scenario
import ru.point.fasting.domain.TimerStatus
import ru.point.fasting.domain.UserTimer

interface TimerRepository {

    fun observeTimer(userId: String): Flow<UserTimer?>

    suspend fun updateFastingBackendInfo(
        userFastingId: String,
        userId: String,
        status: String,
        startTimeMillis: Long?,
        endTimeMillis: Long?,
        eatingWhileFast: Boolean,
        isActive: Boolean,
        lastUpdateMillis: Long
    ): UpdateFastingBackendInfoResult

    suspend fun fetchAndCache(userId: String)

    suspend fun getScenarioById(scenarioID: String): Scenario?

    suspend fun updateUserScenario(userId: String, scenarioId: String): UpdateFastingBackendInfoResult

    suspend fun clearLocalData()

    suspend fun getCurrentTimer(userId: String): UserTimer?

    suspend fun updateTimer(
        userId: String,
        status: TimerStatus,
        startMillis: Long,
        endMillis: Long
    )

    suspend fun updateStatusOff(userId: String)

    suspend fun togglePhase(userId: String)

    suspend fun updateStartTime(userId: String, newStartMillis: Long)

    suspend fun adjustTimerWindow(
        userId: String,
        newPhase: TimerStatus,
        newStartMillis: Long,
        newEndMillis: Long
    )
}