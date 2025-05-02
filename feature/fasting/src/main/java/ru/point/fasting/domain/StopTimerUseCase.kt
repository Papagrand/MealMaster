package ru.point.fasting.domain

import ru.point.fasting.data.TimerRepository
import ru.point.fasting.ui.AlarmScheduler
import javax.inject.Inject

class StopTimerUseCase @Inject constructor(
    private val repo: TimerRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(userId: String) {
        // Останавливаем во всех фазах
        repo.updateStatusOff(userId)

        // Отменяем все запланированные будильники для этого userId
        TimerStatus.values().forEach { phase ->
            alarmScheduler.cancelPhaseChange(userId, phase)
        }
    }
}