package ru.point.fasting.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.point.fasting.domain.TimerStatus
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    private val app: Application
) {
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedulePhaseChange(userId: String, triggerAtMillis: Long, newPhase: TimerStatus) {
        val intent = Intent(app, PhaseChangeReceiver::class.java).apply {
            action = PhaseChangeReceiver.ACTION_PHASE_CHANGE
            putExtra(PhaseChangeReceiver.EXTRA_USER_ID, userId)
            putExtra(PhaseChangeReceiver.EXTRA_NEW_PHASE, newPhase.name)
        }
        val requestCode = (userId.hashCode() shl 1) or newPhase.ordinal
        val pi = PendingIntent.getBroadcast(
            app,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pi
        )
    }

    fun cancelPhaseChange(userId: String, phase: TimerStatus) {
        val intent = Intent(app, PhaseChangeReceiver::class.java).apply {
            action = PhaseChangeReceiver.ACTION_PHASE_CHANGE
        }
        val requestCode = (userId.hashCode() shl 1) or phase.ordinal
        val pi = PendingIntent.getBroadcast(
            app,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi)
    }

    fun cancelAll(userId: String) {
        TimerStatus.values().forEach { phase ->
            cancelPhaseChange(userId, phase)
        }
    }
}