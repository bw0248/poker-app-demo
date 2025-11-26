package io.github.bw0248.simple.poker.tech_demo

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

actual object Logger {
    private val logger = KotlinLogging.logger { }
    actual fun info(tag: String, message: String) {
        logger.info { "[$tag] $message" }
    }

    actual fun error(tag: String, message: String) {
    }
}
