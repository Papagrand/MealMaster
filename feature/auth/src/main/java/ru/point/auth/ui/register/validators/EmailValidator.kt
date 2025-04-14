package ru.point.auth.ui.register.validators

object EmailValidator {

    fun isValid(email: String): Boolean {
        // Регулярное выражение для базовой проверки email
        val emailRegex = Regex("^[A-Za-z0-9]+([._-][A-Za-z0-9]+)*@[A-Za-z0-9-]+(\\.[A-Za-z]{2,})+\$")
        return emailRegex.matches(email)
    }
}