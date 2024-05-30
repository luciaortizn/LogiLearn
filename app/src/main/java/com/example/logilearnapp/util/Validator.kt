package com.example.logilearnapp.util

object Validator {
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.length in 2..20 && name.all { it.isLetter() }
    }

    fun isValidSurname(surname: String): Boolean {
        return surname.isNotEmpty() && surname.length in 2..20 && surname.all { it.isLetter() }
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false

        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val hasNoWhitespace = password.none { it.isWhitespace() }
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar && hasNoWhitespace
    }
}