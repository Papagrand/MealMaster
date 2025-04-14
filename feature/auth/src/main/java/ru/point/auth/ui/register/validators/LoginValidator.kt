package ru.point.auth.ui.register.validators


object LoginValidator {

    fun isValid(login: String): Boolean {
        val loginRegex = Regex("^[A-Za-z0-9_-]{3,20}$")
        return loginRegex.matches(login)
    }
}
