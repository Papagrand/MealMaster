package ru.point.mealmaster

import android.app.Application
import ru.point.auth.ui.login.di.LoginDepsStore
import ru.point.auth.ui.on_boarding.di.OnboardingDepsStore
import ru.point.auth.ui.register.di.RegistrationDepsStore
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.mealmaster.di.AppComponent
import ru.point.mealmaster.di.DaggerAppComponent

class App : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            // Передача приложения, если необходимо (например, через модуль ApplicationModule)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        SecurePrefs.init(this)
        RegistrationDepsStore.deps = appComponent
        LoginDepsStore.deps = appComponent
        OnboardingDepsStore.deps = appComponent
    }
}