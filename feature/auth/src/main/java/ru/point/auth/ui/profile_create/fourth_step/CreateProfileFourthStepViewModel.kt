package ru.point.auth.ui.profile_create.fourth_step

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

data class FourthStepState(
    val targetWeight: String = "",
    val targetWeightError: String? = null
)

class CreateProfileFourthStepViewModel(
    private val validateTargetWeightUseCase: ValidateTargetWeightUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FourthStepState())
    val state: StateFlow<FourthStepState> = _state.asStateFlow()

    // Храним сырой ввод для целевого веса
    private val _rawTargetWeightInput = MutableStateFlow("")


    fun onTargetWeightChanged(input: String) {
        _rawTargetWeightInput.value = input.trim()
    }

    fun validateAndFormatTargetWeight() {
        val result = validateTargetWeightUseCase.invoke(_rawTargetWeightInput.value)
        _state.value = _state.value.copy(
            targetWeight = result.formattedValue,
            targetWeightError = result.errorMessage
        )
    }
}