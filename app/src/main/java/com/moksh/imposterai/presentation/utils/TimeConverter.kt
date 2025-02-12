package com.moksh.imposterai.presentation.utils

fun Int.toTimerString(): String {
    val time = this

    val minute = "0${time / 60}"
    val seconds = time % 60
    var secondsString = seconds.toString()

    if (seconds < 10) secondsString = "0${seconds}"
    return "$minute:$secondsString"
}