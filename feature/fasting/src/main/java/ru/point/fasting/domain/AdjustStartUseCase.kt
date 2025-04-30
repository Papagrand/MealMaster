package ru.point.fasting.domain

import ru.point.fasting.data.TimerRepository
import javax.inject.Inject

class AdjustStartUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(userId: String, newStart: Long) {
        timerRepository.adjustStart(userId, newStart)
    }
}