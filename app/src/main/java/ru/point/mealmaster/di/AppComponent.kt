package ru.point.mealmaster.di

import dagger.Component
import dagger.Module
import dagger.Provides
import ru.point.api.login.LoginService
import ru.point.api.login.createLoginService
import ru.point.api.profile_creating.ProfileService
import ru.point.api.profile_creating.createProfileService
import ru.point.api.registration.RegistrationService
import ru.point.api.registration.createRegistrationService
import ru.point.auth.ui.login.LoginDeps
import ru.point.auth.ui.on_boarding.OnboardingDeps
import ru.point.auth.ui.register.RegistrationDeps

@Component(modules = [NetworkModule::class, /* другие модули */])
interface AppComponent : RegistrationDeps, LoginDeps, OnboardingDeps {
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
    fun provideProfileService(): ProfileService{
        return createProfileService("http://192.168.1.101:8080")
    }
}