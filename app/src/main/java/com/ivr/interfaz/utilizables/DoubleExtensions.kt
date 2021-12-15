package com.example.metodixrefactor.utils

import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.Formato1Digito(): Double {
    val factor = 10.0.pow(2.toDouble())
    return (this * factor).roundToInt() / factor
}

fun Double.Formato2Digitos(): Double {
    val factor = 10.0.pow(2.toDouble())
    return (this * factor).roundToInt() / factor
}

fun Double.Formato4Digitos(): Double {
    val factor = 10.0.pow(4.toDouble())
    return (this * factor).roundToInt() / factor
}