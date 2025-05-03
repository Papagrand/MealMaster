package ru.point.fasting.domain

import ru.point.fasting.data.TimerRepository
import ru.point.fasting.ui.AlarmScheduler
import javax.inject.Inject

class StartTimerUseCase @Inject constructor(
    private val repo: TimerRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(userId: String) {
        val ut = repo.getCurrentTimer(userId) ?: return
        val now = System.currentTimeMillis()
        val fastMs = ut.scenario.fastingHours * 3600_000L
        val eatMs = ut.scenario.eatingHours * 3600_000L

        repo.updateTimer(
            userId = userId,
            status = TimerStatus.FASTING,
            startMillis = now,
            endMillis = now + fastMs
        )

        alarmScheduler.schedulePhaseChange(userId, now + fastMs, TimerStatus.EATING)
        alarmScheduler.schedulePhaseChange(userId, now + fastMs + eatMs, TimerStatus.FASTING)
    }
}