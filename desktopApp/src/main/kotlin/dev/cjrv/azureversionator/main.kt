package dev.cjrv.azureversionator

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.cjrv.azureversionator.ui.composables.appIconPainter

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Azure Versionator",
        icon = appIconPainter(),
    ) {
        App()
    }
}