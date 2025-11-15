package io.github.bw0248.simple.poker.tech_demo

import android.util.Log

actual object Logger {
    actual fun info(tag: String, message: String) {
        Log.i(tag, message)
    }
}