package ru.point.auth.ui.login.di

import dagger.Module
import dagger.Provides
import ru.point.api.login.data.LoginRepositoryImpl
import ru.point.api.login.data.LoginService
import ru.point.api.login.domain.LoginRepository
import ru.point.auth.ui.login.domain.CheckConnectionUseCase
import ru.point.auth.ui.login.domain.CheckLoginAuthUseCase
import ru.point.auth.ui.login.domain.CheckProfileExistUseCase
import ru.point.auth.ui.login.domain.LoginUserUseCase
import ru.point.auth.ui.login.ui.LoginViewModelFactory

@Module
object LoginModule {

    @Provides
    fun provideLoginRepository(service: LoginService): LoginRepository = LoginRepositoryImpl(service)

    @Provides
    fun provideCheckLoginAuthUseCase(repo: LoginRepository) = CheckLoginAuthUseCase(repo)

    @Provides
    fun provideCheckProfileExistUseCase(repo: LoginRepository) = CheckProfileExistUseCase(repo)

    @Provides
    fun provideLoginUserUseCase(repo: LoginRepository) = LoginUserUseCase(repo)

    @Provides
    fun provideCheckConnectionUseCase(repo: LoginRepository) = CheckConnectionUseCase(repo)

    @Provides
    fun provideLoginViewModelFactory(
        checkLoginAuthUseCase: CheckLoginAuthUseCase,
        loginUserUseCase: LoginUserUseCase,
        checkProfileExistUseCase: CheckProfileExistUseCase,
        checkConnectionUseCase: CheckConnectionUseCase
    ) = LoginViewModelFactory(
            checkLoginAuthUseCaseProvider = { checkLoginAuthUseCase },
            loginUserUseCaseProvider = { loginUserUseCase },
            checkProfileExistUseCaseProvider = { checkProfileExistUseCase },
            checkConnectionUseCaseProvider = { checkConnectionUseCase }
        )
}