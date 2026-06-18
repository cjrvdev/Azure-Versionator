package dev.cjrv.azureversionator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.cjrv.azureversionator.ui.HomeScreen

/**
 * Central navigation graph wired to [NavDisplay].
 *
 * @param backStack mutable list acting as the navigation back stack.
 * @param modifier applied to the [NavDisplay] root.
 */
@Composable
fun AppNavigation(
    backStack: MutableList<Screen>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { screen ->
            when (screen) {
                is HomeScreen -> NavEntry(screen) {
                    HomeScreen(
                        // Example: navigate to a detail screen
                        // onNavigateToDetail = { id -> backStack.add(DetailScreen(id)) }
                    )
                }
            }
        },
    )
}

