package ru.point.auth.ui.register

object PasswordValidator {

    /**
     * Проверяет, что пароль имеет длину не менее 8 символов.
     *
     * @param password строка с паролем.
     * @return true, если длина пароля не менее 8 символов, иначе false.
     */
    fun isLongEnough(password: String): Boolean {
        return password.length >= 8
    }

    /**
     * Проверяет, что пароль содержит только допустимые символы:
     * латинские буквы (A-Z, a-z), цифры (0-9) и знаки препинания/специальные символы,
     * представленные диапазоном ASCII от '!' до '~'.
     *
     * @param password строка с паролем.
     * @return true, если пароль содержит только допустимые символы, иначе false.
     */
    fun containsOnlyAllowedCharacters(password: String): Boolean {
        // Диапазон [!-~] соответствует всем печатным ASCII-символам, за исключением пробела.
        val regex = Regex("^[!-~]+\$")
        return regex.matches(password)
    }

    /**
     * Общая функция для проверки валидности пароля.
     * Пароль считается корректным, если его длина не менее 8 символов
     * и он содержит только допустимые символы (латиница, цифры, знаки).
     *
     * @param password строка с паролем.
     * @return true, если пароль удовлетворяет обоим условиям, иначе false.
     */
    fun isValid(password: String): Boolean {
        return isLongEnough(password) && containsOnlyAllowedCharacters(password)
    }
}
