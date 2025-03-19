package ru.point.core.secure_prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefs {

    private const val PREF_FILE_NAME = "my_app_secure_prefs"
    private var sharedPrefs: SharedPreferences? = null

    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPrefs = EncryptedSharedPreferences.create(
            context,
            PREF_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Сохранение userId при логине
    fun saveUserId(userId: String) {
        sharedPrefs?.edit()?.putString("user_id", userId)?.apply()
    }

    // Получение userId в любом месте приложения
    fun getUserId(): String? {
        return sharedPrefs?.getString("user_id", null)
    }

    // Удаление userId (например, при логауте)
    fun removeUserId() {
        sharedPrefs?.edit()?.remove("user_id")?.apply()
    }
}