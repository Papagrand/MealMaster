package ru.point.fasting.domain

import ru.point.fasting.data.TimerRepository
import ru.point.fasting.ui.AlarmScheduler
import javax.inject.Inject

class AdjustStartUseCase @Inject constructor(
    private val repo: TimerRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(userId: String, newStartMillis: Long) {
        // Читаем текущее состояние
        val ut = repo.getCurrentTimer(userId) ?: return
        // Определяем длительность текущей фазы
        val hours = when (ut.status) {
            TimerStatus.FASTING -> ut.scenario.eatingHours
            TimerStatus.EATING -> ut.scenario.fastingHours
            else -> return
        }
        val newPhase = when (ut.status) {
            TimerStatus.FASTING -> TimerStatus.EATING
            TimerStatus.EATING -> TimerStatus.FASTING
            else -> return
        }
        val newEnd = newStartMillis + hours * 3600_000L

        repo.adjustTimerWindow(
            userId = userId,
            newPhase = newPhase,
            newStartMillis = newStartMillis,
            newEndMillis = newEnd
        )

        alarmScheduler.cancelPhaseChange(userId, ut.status)
        alarmScheduler.schedulePhaseChange(userId, newEnd, newPhase)
    }
}