package ru.point.auth.ui.register.validators


object LoginValidator {

    /**
     * Проверяет, корректно ли введён логин.
     *
     * Допустимые символы:
     * - Заглавные и строчные буквы (A-Z, a-z)
     * - Цифры (0-9)
     * - Символы подчёркивания (_) и дефиса (-)
     *
     * Недопустимы любые другие символы (например, вопросительные знаки, решётки и т.д.).
     * Логин должен содержать от 3 до 20 символов.
     *
     * @param login строка с логином.
     * @return true, если логин соответствует требованиям, иначе false.
     */
    fun isValid(login: String): Boolean {
        val loginRegex = Regex("^[A-Za-z0-9_-]{3,20}$")
        return loginRegex.matches(login)
    }
}
