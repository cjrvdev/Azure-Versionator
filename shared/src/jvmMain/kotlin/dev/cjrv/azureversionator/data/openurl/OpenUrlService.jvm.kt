package dev.cjrv.azureversionator.data.openurl

import java.awt.Desktop
import java.net.URI

actual class OpenUrlService {
    actual fun openInBrowser(url: String): Boolean = try {
        val uri = URI(url)
        when {
            Desktop.isDesktopSupported() && Desktop.getDesktop()
                .isSupported(Desktop.Action.BROWSE) -> {
                Desktop.getDesktop().browse(uri)
                true
            }

            isLinux() -> xdgOpen(url)

            else -> false
        }
    } catch (_: Exception) {
        false
    }

    private fun isLinux(): Boolean =
        System.getProperty("os.name")?.contains("linux", ignoreCase = true) == true

    private fun xdgOpen(url: String): Boolean = try {
        Runtime.getRuntime().exec(arrayOf("xdg-open", url))
        true
    } catch (_: Exception) {
        false
    }
}