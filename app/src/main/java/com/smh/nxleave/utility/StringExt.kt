package com.smh.nxleave.utility

fun String.isEmail(): Boolean {
    val emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()
    return emailRegex.matches(this)
}

fun String.isPassword(): Boolean {
    val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}\$".toRegex()
    return passwordRegex.matches(this)
}

fun String.removeWhiteSpaces(): String {
    return this.replace(" ", "")
}