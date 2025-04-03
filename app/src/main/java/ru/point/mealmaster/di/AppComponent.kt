package ru.point.mealmaster.di

import dagger.Component
import dagger.Module
import dagger.Provides
import ru.point.api.login.data.LoginService
import ru.point.api.login.data.createLoginService
import ru.point.api.profile_creating.data.ProfileService
import ru.point.api.profile_creating.data.createProfileService
import ru.point.api.recipes.data.RecipeService
import ru.point.api.recipes.data.createRecipeService
import ru.point.api.registration.data.RegistrationService
import ru.point.api.registration.data.createRegistrationService
import ru.point.auth.ui.login.di.LoginDeps
import ru.point.auth.ui.on_boarding.di.OnboardingDeps
import ru.point.auth.ui.register.di.RegistrationDeps
import ru.point.recipe_information.di.RecipeInformationDeps

@Component(modules = [NetworkModule::class, /* другие модули */])
interface AppComponent : RegistrationDeps, LoginDeps, OnboardingDeps, RecipeInformationDeps {
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
    fun provideProfileService(): ProfileService {
        return createProfileService("http://192.168.1.101:8080")
    }

    @Provides
    fun provideRecipeService(): RecipeService {
        return createRecipeService("http://192.168.1.101:8080")
    }
}