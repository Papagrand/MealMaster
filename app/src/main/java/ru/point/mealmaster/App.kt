package ru.point.mealmaster

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ru.point.auth.ui.login.di.LoginDepsStore
import ru.point.auth.ui.on_boarding.di.OnboardingDepsStore
import ru.point.auth.ui.register.di.RegistrationDepsStore
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.secure_prefs.ThemeMode
import ru.point.fasting.di.TimerFragmentDepsStore
import ru.point.home.di.HomeFragmentDepsStore
import ru.point.meal.di.MealProductSearchFragmentDepsStore
import ru.point.mealmaster.di.AppComponent
import ru.point.mealmaster.di.AppModule
import ru.point.mealmaster.di.DaggerAppComponent
import ru.point.profile.di.ProfileDepsStore
import ru.point.profile.di.UpdateProfileDepsStore
import ru.point.recipe_information.di.RecipeInformationDepsStore
import ru.point.recipes.di.RecipesFragmentDepsStore
import ru.point.setting_searched_product.di.SettingSearchedProductFragmentDepsStore

class App : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        SecurePrefs.init(this)
        RegistrationDepsStore.deps = appComponent
        LoginDepsStore.deps = appComponent
        OnboardingDepsStore.deps = appComponent
        HomeFragmentDepsStore.deps = appComponent
        RecipeInformationDepsStore.deps = appComponent
        ProfileDepsStore.deps = appComponent
        UpdateProfileDepsStore.deps = appComponent
        MealProductSearchFragmentDepsStore.deps = appComponent
        SettingSearchedProductFragmentDepsStore.deps = appComponent
        RecipesFragmentDepsStore.deps = appComponent
        TimerFragmentDepsStore.deps = appComponent

        SecurePrefs.init(this)

        val mode = when (SecurePrefs.getTheme()) {
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}