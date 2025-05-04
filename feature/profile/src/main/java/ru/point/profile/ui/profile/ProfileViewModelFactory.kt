package ru.point.profile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.core.LogoutHandler
import ru.point.profile.domain.GetProfileMainDataUseCase
import ru.point.profile.domain.LogoutUserUseCase
import javax.inject.Inject
import javax.inject.Provider

class ProfileViewModelFactory @Inject constructor(
    private val getProfileMainDataUseCaseProvider: Provider<GetProfileMainDataUseCase>,
    private val logoutUserUseCaseProvider: Provider<LogoutUserUseCase>,
    private val logoutHandlersProvider: Provider<Set<@JvmSuppressWildcards LogoutHandler>>,

    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ProfileViewModel::class.java)
        return ProfileViewModel(
            getProfileMainDataUseCaseProvider.get(),
            logoutUserUseCaseProvider.get(),
            logoutHandlersProvider.get()
            ) as T
    }
}