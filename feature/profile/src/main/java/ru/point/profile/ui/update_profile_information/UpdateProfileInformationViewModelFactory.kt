package ru.point.profile.ui.update_profile_information

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.profile.domain.UpdateProfileInformationUseCase
import javax.inject.Inject
import javax.inject.Provider
import ru.point.profile.domain.UpdateProfileValidationUseCase

class UpdateProfileInformationViewModelFactory @Inject constructor(
    private val updateProfileValidationUseCaseProvider: Provider<UpdateProfileValidationUseCase>,
    private val updateProfileInformationUseCaseProvider: Provider<UpdateProfileInformationUseCase>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == UpdateProfileInformationViewModel::class.java)
        return UpdateProfileInformationViewModel(
            updateProfileValidationUseCaseProvider.get(),
            updateProfileInformationUseCaseProvider.get()
        ) as T
    }

}