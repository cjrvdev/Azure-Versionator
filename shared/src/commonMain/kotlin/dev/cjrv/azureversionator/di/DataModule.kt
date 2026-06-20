package dev.cjrv.azureversionator.di

import com.russhwolf.settings.Settings
import dev.cjrv.azureversionator.data.network.AzureDevOpsApi
import dev.cjrv.azureversionator.data.network.AzureDevOpsApiImpl
import dev.cjrv.azureversionator.data.network.defaultHttpEngine
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    single<Settings> { Settings() }

    single<AzureSettingsRepository> { AzureSettingsRepositoryImpl(get()) }

    single<HttpClient> {
        HttpClient(defaultHttpEngine()) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    single<AzureDevOpsApi> { AzureDevOpsApiImpl(get()) }
}

