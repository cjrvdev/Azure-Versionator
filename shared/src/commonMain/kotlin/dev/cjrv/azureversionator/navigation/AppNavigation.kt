package dev.cjrv.azureversionator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.NavDisplay
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
@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppNavigation(
    backStack: MutableList<Screen>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = koinEntryProvider(),
    )
}
