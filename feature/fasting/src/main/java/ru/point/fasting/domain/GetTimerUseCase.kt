package ru.point.fasting.domain

import kotlinx.coroutines.flow.Flow
import ru.point.api.timer_fasting.data.TimerService
import ru.point.fasting.data.TimerRepository
import javax.inject.Inject

class GetTimerUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {

    suspend fun initialFetch(userId: String) {
        timerRepository.fetchAndCache(userId)
    }

    operator fun invoke(userId: String): Flow<UserTimer?> {
        return timerRepository.observeTimer(userId)
    }
}