package ru.point.mealmaster

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.point.api.login.data.LoginService
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core_data.SessionRepository

class MainViewModel(
    private val context: Context,
    private val repo: SessionRepository,
    private val loginService: LoginService
) : ViewModel() {

    enum class Destination { LOGIN, ONBOARD, MAIN }

    var isLoading: Boolean = true
        private set

    private val _contentLoaded = MutableStateFlow(false)
    val contentLoaded: StateFlow<Boolean> = _contentLoaded.asStateFlow()

    fun resolve(onResult: (Destination) -> Unit) {
        viewModelScope.launch {
            val deviceId = SecurePrefs.getString("device_id")
                ?: android.provider.Settings.Secure.getString(
                    context.contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )

            // Логика проверки
            val dest = withContext(Dispatchers.IO) {
                val userId = SecurePrefs.getUserId()
                if (userId.isNullOrEmpty()) {
                    Destination.LOGIN
                } else if (repo.isAuthorized()) {
                    if (repo.hasProfile()) Destination.MAIN else Destination.ONBOARD
                } else {
                    try {
                        val authOk = loginService
                            .checkAuthUser(userId, deviceId)
                            .body()?.success == true
                        repo.setAuthorized(authOk)
                        if (!authOk) {
                            Destination.LOGIN
                        } else {
                            val hasProfile = loginService
                                .checkProfileExist(userId)
                                .code() == 200
                            repo.setHasProfile(hasProfile)
                            if (hasProfile) Destination.MAIN else Destination.ONBOARD
                        }
                    } catch (e: Exception) {
                        Destination.LOGIN
                    }
                }
            }

            isLoading = false
            onResult(dest)
        }
    }

    fun markContentLoaded() {
        _contentLoaded.value = true
    }
}