package dev.cjrv.azureversionator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

