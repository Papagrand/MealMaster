package ru.point.fasting.domain

import kotlinx.coroutines.flow.Flow
import ru.point.fasting.data.TimerRepository
import javax.inject.Inject

class GetTimerUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    operator fun invoke(userId: String): Flow<UserTimer?> {
        return timerRepository.observeTimer(userId)
    }
}