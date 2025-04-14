package ru.point.auth.ui.register

object PasswordValidator {

    fun isLongEnough(password: String): Boolean {
        return password.length >= 8
    }

    fun containsOnlyAllowedCharacters(password: String): Boolean {
        // Диапазон [!-~] соответствует всем печатным ASCII-символам, за исключением пробела.
        val regex = Regex("^[!-~]+\$")
        return regex.matches(password)
    }

    fun isValid(password: String): Boolean {
        return isLongEnough(password) && containsOnlyAllowedCharacters(password)
    }
}
