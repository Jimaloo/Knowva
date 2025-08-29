package com.knowva.app.services

import org.mindrot.jbcrypt.BCrypt

// Password Service
class PasswordService {
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return BCrypt.checkpw(password, hash)
    }

    fun validatePasswordStrength(password: String): List<String> {
        val errors = mutableListOf<String>()
        if (password.length < 8) errors.add("Password must be at least 8 characters long")
        if (password.length > 128) errors.add("Password must be less than 128 characters long")
        if (!password.any { it.isDigit() }) errors.add("Password must contain at least one number")
        if (!password.any { it.isUpperCase() }) errors.add("Password must contain at least one uppercase letter")
        if (!password.any { it.isLowerCase() }) errors.add("Password must contain at least one lowercase letter")
        if (!password.any { "!@#$%^&*()_+-=[]{}|;:,.<>?".contains(it) }) {
            errors.add("Password must contain at least one special character")
        }
        return errors
    }
}