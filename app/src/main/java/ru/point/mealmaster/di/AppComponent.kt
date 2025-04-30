package ru.point.mealmaster.di

import dagger.Component
import dagger.Module
import dagger.Provides
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionService
import ru.point.api.daily_consumption_and_product.data.createDailyConsumptionService
import ru.point.api.login.data.LoginService
import ru.point.api.login.data.createLoginService
import ru.point.api.meal.data.MealService
import ru.point.api.meal.data.createMealService
import ru.point.api.profile_creating.data.ProfileCreateService
import ru.point.api.profile_creating.data.createProfileCreateService
import ru.point.api.profile_data.data.ProfileDataService
import ru.point.api.profile_data.data.createProfileDataService
import ru.point.api.recipes.data.RecipeService
import ru.point.api.recipes.data.createRecipeService
import ru.point.api.registration.data.RegistrationService
import ru.point.api.registration.data.createRegistrationService
import ru.point.api.timer_fasting.data.TimerService
import ru.point.api.timer_fasting.data.createTimerService
import ru.point.auth.ui.login.di.LoginDeps
import ru.point.auth.ui.on_boarding.di.OnboardingDeps
import ru.point.auth.ui.register.di.RegistrationDeps
import ru.point.core_data.DatabaseModule
import ru.point.fasting.di.TimerFragmentDeps
import ru.point.home.di.HomeFragmentDeps
import ru.point.meal.di.MealProductSearchFragmentDeps
import ru.point.profile.di.ProfileDeps
import ru.point.profile.di.UpdateProfileDeps
import ru.point.recipe_information.di.RecipeInformationDeps
import ru.point.recipes.di.RecipesFragmentDeps
import ru.point.setting_searched_product.di.SettingSearchedProductFragmentDeps

@Component(modules = [AppModule::class, NetworkModule::class, DatabaseModule::class])
interface AppComponent : RegistrationDeps, LoginDeps, OnboardingDeps, RecipeInformationDeps,
    ProfileDeps, UpdateProfileDeps, HomeFragmentDeps, MealProductSearchFragmentDeps,
    SettingSearchedProductFragmentDeps, RecipesFragmentDeps, TimerFragmentDeps {
    // Другие глобальные зависимости
}


@Module
class NetworkModule {
    val url = "http://192.168.1.101:8080"

    @Provides
    fun provideRegistrationService(): RegistrationService {
        // Используем наш метод создания сервиса с базовым URL
        return createRegistrationService(url)
    }

    @Provides
    fun provideLoginService(): LoginService {
        return createLoginService(url)
    }

    @Provides
    fun provideProfileCreateService(): ProfileCreateService {
        return createProfileCreateService(url)
    }

    @Provides
    fun provideRecipeService(): RecipeService {
        return createRecipeService(url)
    }

    @Provides
    fun provideProfileDataService(): ProfileDataService {
        return createProfileDataService(url)
    }

    @Provides
    fun provideDailyConsumptionService(): DailyConsumptionService {
        return createDailyConsumptionService(url)
    }

    @Provides
    fun provideMealService(): MealService {
        return createMealService(url)
    }

    @Provides
    fun provideTimerService(): TimerService {
        return createTimerService(url)
    }
}