package ru.point.auth.ui.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.auth.ui.login.domain.CheckLoginAuthUseCase
import ru.point.auth.ui.login.domain.CheckProfileExistUseCase
import ru.point.auth.ui.login.domain.LoginUserUseCase
import javax.inject.Inject
import javax.inject.Provider

class LoginViewModelFactory @Inject constructor(
    private val checkLoginAuthUseCaseProvider: Provider<CheckLoginAuthUseCase>,
    private val loginUserUseCaseProvider: Provider<LoginUserUseCase>,
    private val checkProfileExistUseCaseProvider: Provider<CheckProfileExistUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == LoginViewModel::class.java)
        return LoginViewModel(
            checkLoginAuthUseCase = checkLoginAuthUseCaseProvider.get(),
            loginUserUseCase = loginUserUseCaseProvider.get(),
            checkProfileExistUseCase = checkProfileExistUseCaseProvider.get()
        ) as T
    }
}
