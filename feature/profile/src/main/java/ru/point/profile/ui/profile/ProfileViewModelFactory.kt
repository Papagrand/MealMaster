package ru.point.profile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.profile.domain.GetProfileMainDataUseCase
import javax.inject.Inject
import javax.inject.Provider

class ProfileViewModelFactory @Inject constructor(
    private val getProfileMainDataUseCaseProvider: Provider<GetProfileMainDataUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ProfileViewModel::class.java)
        return ProfileViewModel(
            getProfileMainDataUseCaseProvider.get()
        ) as T
    }
}