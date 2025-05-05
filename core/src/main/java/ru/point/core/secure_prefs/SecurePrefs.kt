package ru.point.core.secure_prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefs {

    private const val PREF_FILE_NAME = "my_app_secure_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_AUTHORIZED = "is_authorized"
    private const val KEY_HAS_PROFILE = "has_profile"

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

    fun saveUserId(userId: String) {
        sharedPrefs?.edit()?.putString(KEY_USER_ID, userId)?.apply()
    }

    fun getUserId(): String? {
        return sharedPrefs?.getString(KEY_USER_ID, null)
    }

    fun removeUserId() {
        sharedPrefs?.edit()?.remove(KEY_USER_ID)?.apply()
    }

    fun saveTheme(mode: ThemeMode) = putString(KEY_THEME_MODE, mode.name.lowercase())
    fun getTheme(): ThemeMode =
        ThemeMode.from(getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name.lowercase())!!)

    fun setAuthorized(v: Boolean) = putBool(KEY_AUTHORIZED, v)
    fun isAuthorized(): Boolean = getBool(KEY_AUTHORIZED)

    fun setHasProfile(v: Boolean) = putBool(KEY_HAS_PROFILE, v)
    fun hasProfile(): Boolean = getBool(KEY_HAS_PROFILE)


    fun putString(key: String, value: String) = sharedPrefs?.edit()?.putString(key, value)?.apply()
    fun getString(key: String, def: String? = null): String? = sharedPrefs?.getString(key, def)

    fun putBool(key: String, value: Boolean) = sharedPrefs?.edit()?.putBoolean(key, value)?.apply()
    fun getBool(key: String, def: Boolean = false): Boolean =
        sharedPrefs?.getBoolean(key, def) ?: def


}

enum class ThemeMode {
    LIGHT, DARK, SYSTEM;

    companion object {
        fun from(src: String) = values().firstOrNull { it.name.equals(src, true) } ?: SYSTEM
    }
}

fun ThemeMode.toNightMode(): Int = when (this) {
    ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
    ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
}