package dev.cjrv.azureversionator.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
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
 * @param backStack mutable list acting as the navigation back stack.
 * @param modifier applied to the [NavDisplay] root.
 */
@Composable
@OptIn(KoinExperimentalAPI::class)
fun Navigation() {
    val entryProvider = koinEntryProvider<Any>()
    val navigator = koinInject<Navigator>()

    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}