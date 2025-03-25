package ru.point.auth.ui.profile_create.first_step

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreateProfileFirstStepViewModelFactory(
    private val profileValidationUseCase: ProfileValidationUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == CreateProfileFirstStepViewModel::class.java)
        return CreateProfileFirstStepViewModel(profileValidationUseCase) as T
    }
}
