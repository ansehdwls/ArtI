package com.hexa.arti.util

fun isPasswordValid(password: String): Boolean {
    val passwordRegex = "^(?=.*[a-zA-Z0-9])(?=.*[\\W_])[a-zA-Z0-9\\W_]{8,20}\$"
    return password.matches(passwordRegex.toRegex())
}