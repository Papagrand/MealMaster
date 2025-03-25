package ru.point.auth.ui.register.di

import dagger.Module
import dagger.Provides
import ru.point.api.registration.data.RegistrationRepositoryImpl
import ru.point.api.registration.data.RegistrationService
import ru.point.api.registration.domain.RegistrationRepository
import ru.point.auth.ui.register.domain.CheckEmailUseCase
import ru.point.auth.ui.register.domain.CheckLoginUseCase
import ru.point.auth.ui.register.domain.RegisterUserUseCase
import ru.point.auth.ui.register.ui.RegistrationViewModelFactory

@Module
object RegistrationModule {

    @Provides
    fun provideRegistrationRepository(
        service: RegistrationService
    ): RegistrationRepository {
        return RegistrationRepositoryImpl(service)
    }

    @Provides
    fun provideCheckEmailUseCase(
        repository: RegistrationRepository
    ): CheckEmailUseCase {
        return CheckEmailUseCase(repository)
    }

    @Provides
    fun provideCheckLoginUseCase(
        repository: RegistrationRepository
    ): CheckLoginUseCase {
        return CheckLoginUseCase(repository)
    }

    @Provides
    fun provideRegisterUserUseCase(
        repository: RegistrationRepository
    ): RegisterUserUseCase {
        return RegisterUserUseCase(repository)
    }

    @Provides
    fun provideRegistrationViewModelFactory(
        checkEmailUseCase: CheckEmailUseCase,
        checkLoginUseCase: CheckLoginUseCase,
        registerUserUseCase: RegisterUserUseCase
    ): RegistrationViewModelFactory {
        return RegistrationViewModelFactory(
            checkEmailUseCaseProvider = { checkEmailUseCase },
            checkLoginUseCaseProvider = { checkLoginUseCase },
            registerUserUseCaseProvider = { registerUserUseCase }
        )
    }
}