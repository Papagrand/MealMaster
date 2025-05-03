package ru.point.fasting.ui

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.point.fasting.R
import ru.point.fasting.di.DaggerTimerComponent
import ru.point.fasting.di.TimerFragmentDepsProvider
import ru.point.fasting.domain.StartTimerUseCase
import ru.point.fasting.domain.StopTimerUseCase
import ru.point.fasting.domain.TimerStatus
import javax.inject.Inject

class PhaseChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var startTimerUseCase: StartTimerUseCase
    @Inject
    lateinit var stopTimerUseCase: StopTimerUseCase

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(ctx: Context, intent: Intent) {

        DaggerTimerComponent.builder()
            .application(ctx.applicationContext as Application)
            .deps(TimerFragmentDepsProvider.deps)
            .build()
            .inject(this)

        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return
        val phaseRaw = intent.getStringExtra(EXTRA_NEW_PHASE) ?: return
        val newPhase = TimerStatus.valueOf(phaseRaw)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        "Fasting timer",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Уведомления о смене фаз голодания/питания"
                    }
                )
            }
        }

        val title = when (newPhase) {
            TimerStatus.FASTING -> "Время голодать"
            TimerStatus.EATING -> "Можно кушать"
            else -> "Таймер остановлен"
        }

        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon3_1)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .build()

        val canNotify = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

        if (canNotify) {
            NotificationManagerCompat.from(ctx).notify(newPhase.ordinal, notification)
        }
    }

    companion object {
        const val ACTION_PHASE_CHANGE = "ru.point.fasting.ACTION_PHASE_CHANGE"
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
        const val EXTRA_NEW_PHASE = "EXTRA_NEW_PHASE"
        const val CHANNEL_ID = "fasting_timer_channel"
    }
}
