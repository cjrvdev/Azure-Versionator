package dev.cjrv.azureversionator

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.cjrv.azureversionator.ui.composables.appIconPainter
import io.github.vinceglb.filekit.FileKit

fun main() {

    FileKit.init("dev.cjrv.azureversionator")

    // Para que FileKit ponga el modo oscuro/claro del sistema en MacOS
    System.setProperty("apple.awt.application.appearance", "system")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Azure Versionator",
            icon = appIconPainter(),
        ) {
            App()
        }
    }
}