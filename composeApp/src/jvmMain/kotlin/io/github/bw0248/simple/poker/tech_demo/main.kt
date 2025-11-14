package io.github.bw0248.simple.poker.tech_demo

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "tech_demo",
    ) {
        window.minimumSize = Dimension(1920, 1080)
        App()
    }
}