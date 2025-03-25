package ru.point.auth.ui.profile_create.fourth_step

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    suspend fun validateAndFormatTargetWeight() {
        val result = validateTargetWeightUseCase.invoke(_rawTargetWeightInput.value)
        _state.value = _state.value.copy(
            targetWeight = result.formattedValue,
            targetWeightError = result.errorMessage
        )
    }
}