package com.smh.nxleave.utility

fun Double.toDays(): String {
    val decimal = this.toString().substringAfter(".").toInt()
    return if (decimal == 0) if (this <= 1) "${this.toInt()} Day" else "${this.toInt()} Days"
    else if (this <= 1) "$this Day" else "$this Days"
}

fun Double.toIntOrDoubleString(): String {
    val decimal = this.toString().substringAfter(".").toInt()
    return if (decimal == 0) "${this.toInt()}"
    else "$this"
}