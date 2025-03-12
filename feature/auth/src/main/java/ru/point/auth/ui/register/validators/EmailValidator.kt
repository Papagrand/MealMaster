package ru.point.auth.ui.register.validators

object EmailValidator {

    /**
     * Проверяет, соответствует ли email базовому формату.
     *
     * Допустимый формат:
     * - Основная часть (до '@') должна состоять из букв и цифр, может содержать точки, подчеркивания или дефисы.
     * - Затем обязательно должен идти символ '@'.
     * - После '@' указывается доменное имя, состоящее из букв, цифр и дефисов, с расширением (например, .com, .ru).
     *
     * Примеры допустимых email:
     * - stam.wind@gmail.com
     * - user_name@mail.ru
     *
     * @param email строка с адресом электронной почты.
     * @return true, если email корректен, иначе false.
     */
    fun isValid(email: String): Boolean {
        // Регулярное выражение для базовой проверки email
        val emailRegex = Regex("^[A-Za-z0-9]+([._-][A-Za-z0-9]+)*@[A-Za-z0-9-]+(\\.[A-Za-z]{2,})+\$")
        return emailRegex.matches(email)
    }
}