package ru.point.auth.ui.profile_create.fourth_step

class ValidateTargetWeightUseCase {

    fun invoke(rawInput: String): ValidationResult {
        var formatted = rawInput
        // Если строка заканчивается на '.', ждём продолжения ввода
        if (formatted.endsWith(".")) {
            // Ошибки нет, просто возвращаем значение,
            // чтобы пользователь мог дописать цифру
            return ValidationResult(formatted, null)
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

                    // Ограничиваем целую часть не более 3 цифр (например, до 999)
                    if (integerPart.length > 3) {
                        return ValidationResult(
                            formatted,
                            "Целевой вес не должен превышать 3 цифры"
                        )
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
            formatted.isEmpty()       -> "Введите целевой вес"
            numberFinal == null       -> "Некорректное значение"
            else                      -> null
        }

        return ValidationResult(formatted, error)
    }
}

data class ValidationResult(
    val formattedValue: String,
    val errorMessage: String?
)