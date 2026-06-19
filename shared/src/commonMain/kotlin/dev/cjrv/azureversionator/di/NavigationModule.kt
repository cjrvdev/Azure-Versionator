package dev.cjrv.azureversionator.di

import dev.cjrv.azureversionator.navigation.Navigator
import dev.cjrv.azureversionator.navigation.Home
import dev.cjrv.azureversionator.ui.features.HomeScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

/**
 * Navigation graph definitions for Koin Navigation 3 integration.
 *
 * Add each screen route here with `navigation<Route> { }`.
 */
@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    single { Navigator(startDestination = Home) }

    navigation<Home> { _ ->
        HomeScreen()
    }
}

