package ru.point.profile.domain

class UpdateProfileValidationUseCase {


    fun validateWeight(rawInput: String): ValidationResult {
        var formatted = rawInput
        // Если строка заканчивается на ".", ждём продолжения
        if (formatted.endsWith(".")) {
            return ValidationResult(formatted, null)
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

                    // Ограничиваем целую часть максимум 3 цифрами,
                    // т.к. вес до 500 – условно трёхзначное число
                    val validIntegerPart = if (integerPart.length > 3) {
                        integerPart.take(3)
                    } else {
                        integerPart
                    }

                    if (decimalPart.length > 1) {
                        decimalPart = decimalPart.substring(0, 1)
                    }

                    // Собираем всё обратно
                    formatted = "$validIntegerPart.${decimalPart}"
                        .trimEnd('.') // если вдруг decimalPart окажется пустой
                }
            }
        }

        val number = formatted.toFloatOrNull()
        val error = when {
            number == null -> ""
            number > 500f -> "Вес не может быть больше 500 кг"
            else -> null
        }
        return ValidationResult(formatted, error)
    }

    fun validateName(rawInput: String, isName: Boolean): ValidationResult {
        // Убираем лишние пробелы по краям
        val trimmedInput = rawInput.trim()
        // Проверяем, что строка не пуста
        if (isName && trimmedInput.isEmpty()) {
            return ValidationResult(trimmedInput, "Поле не может быть пустым")
        }
        // Регулярное выражение, которое разрешает только латинские буквы
        val latinLettersOnly = Regex("^[a-zA-Z]+$")
        if (!latinLettersOnly.matches(trimmedInput)) {
            return ValidationResult(trimmedInput, "Поле может содержать только латинские буквы")
        }
        return ValidationResult(trimmedInput, null)
    }

    fun validateAge(rawInput: String): ValidationResult {
        val formatted = rawInput.trim()
        val number = formatted.toIntOrNull()
        val error = when {
            formatted.isEmpty() -> "Введите возраст"
            number == null -> "Некорректное значение"
            number < 14 -> "Пользователь должен быть старше 14 лет"
            number > 120 -> "Возраст не может быть больше 120"
            else -> null
        }
        return ValidationResult(formatted, error)
    }
}

data class ValidationResult(
    val formattedValue: String,
    val errorMessage: String?
)