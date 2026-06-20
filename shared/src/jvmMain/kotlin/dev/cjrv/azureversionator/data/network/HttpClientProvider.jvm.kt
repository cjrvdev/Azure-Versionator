package dev.cjrv.azureversionator.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun defaultHttpEngine(): HttpClientEngine = CIO.create()

