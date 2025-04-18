package ru.point.auth.ui.profile_create.first_step

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FirstStepState(
    val height: String = "",
    val heightError: String? = null,
    val weight: String = "",
    val weightError: String? = null,
    val age: String = "",
    val ageError: String? = null
)

class CreateProfileFirstStepViewModel(
    private val profileValidationUseCase: ProfileValidationUseCase
) : ViewModel() {

    //private val _state = MutableStateFlow(FirstStepState())

    val state: StateFlow<FirstStepState>
        field = MutableStateFlow(FirstStepState())

    private val _rawHeightInput = MutableStateFlow("")
    private val _rawWeightInput = MutableStateFlow("")

    fun onHeightChanged(input: String) {
        _rawHeightInput.value = input.trim()
    }

    fun processHeight() {
        val result = profileValidationUseCase.validateHeight(_rawHeightInput.value)
        state.value = state.value.copy(
            height = result.formattedValue,
            heightError = result.errorMessage
        )
    }

    fun onWeightChanged(input: String) {
        _rawWeightInput.value = input.trim()
    }

    fun processWeight() {
        val result = profileValidationUseCase.validateWeight(_rawWeightInput.value)
        state.value = state.value.copy(
            weight = result.formattedValue,
            weightError = result.errorMessage
        )
    }

    fun onAgeChanged(input: String) {
        // Здесь можем вызывать сразу validateAge
        val result = profileValidationUseCase.validateAge(input.trim())
        state.value = state.value.copy(
            age = result.formattedValue,
            ageError = result.errorMessage
        )
    }
}
