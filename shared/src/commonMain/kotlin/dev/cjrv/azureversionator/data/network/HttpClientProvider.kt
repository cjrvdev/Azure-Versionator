package dev.cjrv.azureversionator.data.network

import io.ktor.client.engine.HttpClientEngine

expect fun defaultHttpEngine(): HttpClientEngine

