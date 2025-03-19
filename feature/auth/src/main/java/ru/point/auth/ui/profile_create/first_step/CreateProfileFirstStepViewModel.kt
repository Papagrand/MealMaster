package ru.point.auth.ui.profile_create.first_step

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

data class FirstStepState(
    val height: String = "",
    val heightError: String? = null,
    val weight: String = "",
    val weightError: String? = null,
    val age: String = "",
    val ageError: String? = null
)

class CreateProfileFirstStepViewModel : ViewModel() {

    private val _state = MutableStateFlow(FirstStepState())
    val state: StateFlow<FirstStepState> = _state

    private val _rawHeightInput = MutableStateFlow("")
    private val _rawWeightInput = MutableStateFlow("")

    init {
        // При изменении _rawHeightInput с задержкой в 300 мс обрабатываем значение
        viewModelScope.launch {
            _rawHeightInput
                .debounce(2000)
                .collectLatest { input ->
                    processHeight(input)
                }
        }
        // Аналогично для веса
        viewModelScope.launch {
            _rawWeightInput
                .debounce(2000)
                .collectLatest { input ->
                    processWeight(input)
                }
        }
    }

    fun onHeightChanged(input: String) {
        _rawHeightInput.value = input.trim()
    }

    private fun processHeight(input: String) {
        var formatted = input
        // Если строка заканчивается на точку, ждем продолжения ввода
        if (formatted.endsWith(".")) {
            _state.value = _state.value.copy(height = formatted, heightError = null)
            return
        }
        if (formatted.isNotEmpty()) {
            val number = formatted.toFloatOrNull()
            if (number != null) {
                if (!formatted.contains(".")) {
                    formatted += ".0"
                } else {
                    val parts = formatted.split(".")
                    if (parts.size >= 2) {
                        val integerPart = parts[0]
                        var decimalPart = parts[1]
                        if (decimalPart.length > 1) {
                            decimalPart = decimalPart.substring(0, 1)
                            formatted = "$integerPart.$decimalPart"
                        }
                    }
                }
            }
        }
        val number = formatted.toFloatOrNull()
        val error = when {
            number == null -> ""
            number < 100f -> "Рост не может быть меньше 100"
            number > 250f -> "Рост не может быть больше 250"
            else -> null
        }
        _state.value = _state.value.copy(height = formatted, heightError = error)
    }

    fun onWeightChanged(input: String) {
        _rawWeightInput.value = input.trim()
    }

    private fun processWeight(input: String) {
        var formatted = input
        if (formatted.endsWith(".")) {
            _state.value = _state.value.copy(weight = formatted, weightError = null)
            return
        }
        if (formatted.isNotEmpty()) {
            val number = formatted.toFloatOrNull()
            if (number != null) {
                if (!formatted.contains(".")) {
                    formatted += ".0"
                } else {
                    val parts = formatted.split(".")
                    val integerPart = parts[0]
                    var decimalPart = if (parts.size > 1) parts[1] else ""
                    // Ограничиваем целую часть максимум 3 цифрами
                    val validIntegerPart = if (integerPart.length > 3) integerPart.take(3) else integerPart
                    if (decimalPart.length > 1) {
                        decimalPart = decimalPart.substring(0, 1)
                    }
                    formatted = "$validIntegerPart${if (formatted.contains(".")) ".$decimalPart" else ""}"
                }
            }
        }
        val number = formatted.toFloatOrNull()
        val error = when {
            number == null -> ""
            number > 500f -> "Вес не может быть больше 500 кг"
            else -> null
        }
        _state.value = _state.value.copy(weight = formatted, weightError = error)
    }

    /**
     * Обработка ввода возраста.
     * Возраст должен быть числовым значением от 14 до 120.
     */
    fun onAgeChanged(input: String) {
        val formatted = input.trim()
        val number = formatted.toIntOrNull()
        val error = when {
            formatted.isEmpty() -> "Введите возраст"
            number == null -> "Некорректное значение"
            number < 14 -> "Пользователь должен быть старше 14 лет"
            number > 120 -> "Возраст не может быть больше 120"
            else -> null
        }
        _state.value = _state.value.copy(age = formatted, ageError = error)
    }
}
