package dev.cjrv.azureversionator.di

import dev.cjrv.azureversionator.navigation.HomeScreen as HomeRoute
import dev.cjrv.azureversionator.ui.HomeScreen
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
    navigation<HomeRoute> { _ ->
        HomeScreen()
    }
}

