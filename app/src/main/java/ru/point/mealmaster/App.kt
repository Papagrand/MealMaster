package ru.point.mealmaster

import android.app.Application
import ru.point.auth.ui.register.RegistrationDepsStore
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
        RegistrationDepsStore.deps = appComponent
    }
}