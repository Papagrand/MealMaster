package ru.point.fasting.data

import ru.point.core.LogoutHandler
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.fasting.ui.AlarmScheduler
import javax.inject.Inject

class FastingLogoutHandler @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) : LogoutHandler {

    override suspend fun onLogout() {
        SecurePrefs.getUserId()
            ?.let { alarmScheduler.cancelAll(it) }
    }
}