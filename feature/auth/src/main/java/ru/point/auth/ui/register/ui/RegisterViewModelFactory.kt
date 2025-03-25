package ru.point.auth.ui.register.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.auth.ui.register.domain.CheckEmailUseCase
import ru.point.auth.ui.register.domain.CheckLoginUseCase
import ru.point.auth.ui.register.domain.RegisterUserUseCase
import javax.inject.Inject
import javax.inject.Provider

class RegistrationViewModelFactory @Inject constructor(
    private val checkEmailUseCaseProvider: Provider<CheckEmailUseCase>,
    private val checkLoginUseCaseProvider: Provider<CheckLoginUseCase>,
    private val registerUserUseCaseProvider: Provider<RegisterUserUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == RegistrationViewModel::class.java)
        return RegistrationViewModel(
            checkEmailUseCase = checkEmailUseCaseProvider.get(),
            checkLoginUseCase = checkLoginUseCaseProvider.get(),
            registerUserUseCase = registerUserUseCaseProvider.get()
        ) as T
    }
}