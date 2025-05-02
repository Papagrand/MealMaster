package ru.point.fasting.domain

import ru.point.fasting.data.TimerRepository
import ru.point.fasting.ui.AlarmScheduler
import javax.inject.Inject

class TogglePhaseUseCase @Inject constructor(
    private val repository: TimerRepository,
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke(userId: String, newStartMillis: Long) {
        repository.togglePhase(userId)

        val ut = repository.getCurrentTimer(userId) ?: return

        val hours = when (ut.status) {
            TimerStatus.FASTING -> ut.scenario.fastingHours
            TimerStatus.EATING -> ut.scenario.eatingHours
            else -> return
        }
        val newEnd = newStartMillis + hours * 3600_000L

        alarmScheduler.cancelPhaseChange(userId, ut.status)
        alarmScheduler.schedulePhaseChange(userId, newEnd, ut.status)
    }
}