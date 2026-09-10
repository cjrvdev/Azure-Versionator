package dev.cjrv.azureversionator.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Central navigation host.
 *
 * The entry provider is resolved automatically from Koin — every `navigation<T> { }` declaration
 * in any loaded module is collected here via [koinEntryProvider].
 * To add a new destination just add `navigation<YourRoute> { }` in the relevant Koin module;
 * no changes to this file needed.
 *
 */
@Composable
@OptIn(KoinExperimentalAPI::class)
fun Navigation() {
    val entryProvider = koinEntryProvider<Any>()
    val navigator = koinInject<Navigator>()

    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        transitionSpec = {
            if (targetState.matchesRoute(EditProfile)) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                fadeIn() togetherWith fadeOut()
            }
        },
        popTransitionSpec = {
            if (initialState.matchesRoute(EditProfile)) {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            } else {
                fadeIn() togetherWith fadeOut()
            }
        },
        predictivePopTransitionSpec = {
            if (initialState.matchesRoute(EditProfile)) {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            } else {
                fadeIn() togetherWith fadeOut()
            }
        },
        entryProvider = entryProvider,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}

private fun Scene<*>.matchesRoute(route: Route): Boolean {
    val routeKey = route.toString()
    val entryContentKey = entries.lastOrNull()?.contentKey

    return key == route || key == routeKey || entryContentKey == route || entryContentKey == routeKey
}