package ru.point.mealmaster.di

import dagger.Component
import dagger.Module
import dagger.Provides
import ru.point.api.login.data.LoginService
import ru.point.api.login.data.createLoginService
import ru.point.api.profile_creating.data.ProfileCreateService
import ru.point.api.profile_creating.data.createProfileCreateService
import ru.point.api.profile_data.data.ProfileDataService
import ru.point.api.profile_data.data.createProfileDataService
import ru.point.api.recipes.data.RecipeService
import ru.point.api.recipes.data.createRecipeService
import ru.point.api.registration.data.RegistrationService
import ru.point.api.registration.data.createRegistrationService
import ru.point.auth.ui.login.di.LoginDeps
import ru.point.auth.ui.on_boarding.di.OnboardingDeps
import ru.point.auth.ui.register.di.RegistrationDeps
import ru.point.profile.di.ProfileDeps
import ru.point.profile.di.UpdateProfileDeps
import ru.point.recipe_information.di.RecipeInformationDeps

@Component(modules = [NetworkModule::class, /* другие модули */])
interface AppComponent : RegistrationDeps, LoginDeps, OnboardingDeps, RecipeInformationDeps, ProfileDeps, UpdateProfileDeps {
    // Другие глобальные зависимости
}


@Module
class NetworkModule {

    @Provides
    fun provideRegistrationService(): RegistrationService {
        // Используем наш метод создания сервиса с базовым URL
        return createRegistrationService("http://192.168.1.101:8080")
    }

    @Provides
    fun provideLoginService(): LoginService {
        return createLoginService("http://192.168.1.101:8080")
    }

    @Provides
    fun provideProfileCreateService(): ProfileCreateService {
        return createProfileCreateService("http://192.168.1.101:8080")
    }

    @Provides
    fun provideRecipeService(): RecipeService {
        return createRecipeService("http://192.168.1.101:8080")
    }

    @Provides
    fun provideProfileDataService(): ProfileDataService {
        return createProfileDataService("http://192.168.1.101:8080")
    }
}