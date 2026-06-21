package dev.cjrv.azureversionator.di

import dev.cjrv.azureversionator.navigation.Navigator
import dev.cjrv.azureversionator.navigation.Home
import dev.cjrv.azureversionator.navigation.NewVersion
import dev.cjrv.azureversionator.navigation.Settings
import dev.cjrv.azureversionator.ui.features.HomeScreen
import dev.cjrv.azureversionator.ui.features.NewVersionScreen
import dev.cjrv.azureversionator.ui.features.SettingsScreen
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

    navigation<Home> {
        HomeScreen() { route ->
            get<Navigator>().navigateTo(route)
        }
    }
    navigation<Settings> {
        SettingsScreen() {
            get<Navigator>().goBack()
        }
    }
    navigation<NewVersion> {
        NewVersionScreen() {
            get<Navigator>().goBack()
        }
    }
}

