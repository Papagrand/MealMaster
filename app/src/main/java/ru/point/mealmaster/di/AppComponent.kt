package ru.point.mealmaster.di

import dagger.Component
import dagger.Module
import dagger.Provides
import ru.point.api.registration.RegistrationService
import ru.point.api.registration.createRegistrationService
import ru.point.auth.ui.register.RegistrationDeps

@Component(modules = [NetworkModule::class, /* другие модули */])
interface AppComponent : RegistrationDeps {
    // Другие глобальные зависимости
}


@Module
class NetworkModule {

    @Provides
    fun provideRegistrationService(): RegistrationService {
        // Используем наш метод создания сервиса с базовым URL
        return createRegistrationService("http://192.168.1.101:8080")
    }
}