package ru.point.core_data

import android.app.Application
import android.content.Context
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.secure_prefs.ThemeMode
import javax.inject.Inject

class SessionRepository @Inject constructor(app: Application) {
    init {
        SecurePrefs.init(app)
    }

    fun saveTheme(m: ThemeMode) = SecurePrefs.saveTheme(m)
    fun theme(): ThemeMode = SecurePrefs.getTheme()

    fun setAuthorized(v: Boolean) = SecurePrefs.setAuthorized(v)
    fun isAuthorized(): Boolean = SecurePrefs.isAuthorized()

    fun setHasProfile(v: Boolean) = SecurePrefs.setHasProfile(v)
    fun hasProfile(): Boolean = SecurePrefs.hasProfile()
}