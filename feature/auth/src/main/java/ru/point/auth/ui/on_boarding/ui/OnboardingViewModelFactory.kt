package ru.point.auth.ui.on_boarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.auth.ui.on_boarding.domain.CheckMassRateUseCase
import ru.point.auth.ui.on_boarding.domain.CreateProfileUseCase
import javax.inject.Inject
import javax.inject.Provider

class OnboardingViewModelFactory @Inject constructor(
    private val createProfileUseCaseProvider: Provider<CreateProfileUseCase>,
    private val checkMassRateUseCaseProvider: Provider<CheckMassRateUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == OnboardingViewModel::class.java)
        return OnboardingViewModel(
            createProfileUseCase = createProfileUseCaseProvider.get(),
            checkMassRateUseCase = checkMassRateUseCaseProvider.get()
        ) as T
    }
}