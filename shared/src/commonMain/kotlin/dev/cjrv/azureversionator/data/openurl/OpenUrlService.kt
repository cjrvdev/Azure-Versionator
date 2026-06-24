package dev.cjrv.azureversionator.data.openurl

expect class OpenUrlService {
    fun openInBrowser(url: String): Boolean
}
