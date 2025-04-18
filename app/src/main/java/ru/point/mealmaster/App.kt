package ru.point.mealmaster

import android.app.Application
import ru.point.auth.ui.login.di.LoginDepsStore
import ru.point.auth.ui.on_boarding.di.OnboardingDepsStore
import ru.point.auth.ui.register.di.RegistrationDepsStore
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.home.di.HomeFragmentDepsStore
import ru.point.meal.di.MealProductSearchFragmentDepsStore
import ru.point.mealmaster.di.AppComponent
import ru.point.mealmaster.di.DaggerAppComponent
import ru.point.profile.di.ProfileDepsStore
import ru.point.profile.di.UpdateProfileDepsStore
import ru.point.recipe_information.di.RecipeInformationDepsStore
import ru.point.setting_searched_product.di.SettingSearchedProductFragmentDepsStore

class App : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
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
    }
}