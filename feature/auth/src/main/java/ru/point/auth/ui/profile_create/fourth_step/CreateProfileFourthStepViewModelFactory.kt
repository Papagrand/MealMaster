package ru.point.auth.ui.profile_create.fourth_step

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreateProfileFourthStepViewModelFactory(
    private val validateTargetWeightUseCase: ValidateTargetWeightUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == CreateProfileFourthStepViewModel::class.java)
        return CreateProfileFourthStepViewModel(validateTargetWeightUseCase) as T
    }
}
