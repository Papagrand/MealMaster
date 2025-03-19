package ru.point.auth.ui.profile_create.fourth_step

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FourthStepState(
    val targetWeight: String = "",
    val targetWeightError: String? = null
)

class CreateProfileFourthStepViewModel : ViewModel() {

    private val _state = MutableStateFlow(FourthStepState())
    val state: StateFlow<FourthStepState> = _state.asStateFlow()

    // Храним сырой ввод для целевого веса
    private val _rawTargetWeightInput = MutableStateFlow("")

    init {
        // Обрабатываем ввод с задержкой (debounce) – даем время пользователю завершить ввод
        viewModelScope.launch {
            _rawTargetWeightInput
                .debounce(2000)
                .collectLatest { input ->
                    processTargetWeight(input)
                }
        }
    }

    fun onTargetWeightChanged(input: String) {
        _rawTargetWeightInput.value = input.trim()
    }

    private fun processTargetWeight(input: String) {
        var formatted = input
        // Если строка заканчивается на точку, ждем продолжения ввода (не форматируем)
        if (formatted.endsWith(".")) {
            _state.value = _state.value.copy(targetWeight = formatted, targetWeightError = null)
            return
        }
        if (formatted.isNotEmpty()) {
            val number = formatted.toFloatOrNull()
            if (number != null) {
                // Если отсутствует десятичная точка, добавляем ".0"
                if (!formatted.contains(".")) {
                    formatted += ".0"
                } else {
                    val parts = formatted.split(".")
                    val integerPart = parts[0]
                    var decimalPart = if (parts.size > 1) parts[1] else ""
                    // Ограничиваем целую часть не более 3 цифр
                    if (integerPart.length > 3) {
                        _state.value = _state.value.copy(
                            targetWeight = formatted,
                            targetWeightError = "Целевой вес не должен превышать 3 цифры"
                        )
                        return
                    }
                    // Оставляем только один символ после точки
                    if (decimalPart.length > 1) {
                        decimalPart = decimalPart.substring(0, 1)
                        formatted = "$integerPart.$decimalPart"
                    }
                }
            }
        }
        val numberFinal = formatted.toFloatOrNull()
        val error = when {
            formatted.isEmpty() -> "Введите целевой вес"
            numberFinal == null -> "Некорректное значение"
            else -> null
        }
        _state.value = _state.value.copy(targetWeight = formatted, targetWeightError = error)
    }
}
