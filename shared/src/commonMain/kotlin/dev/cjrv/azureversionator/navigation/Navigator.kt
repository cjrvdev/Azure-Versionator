package dev.cjrv.azureversionator.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Navigator(startDestination: Any) {
    val backStack: SnapshotStateList<Any> = mutableStateListOf(startDestination)

    fun navigateTo(destination: Any) {
        backStack.add(destination)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }

    fun goBackTo(destination: Any) {
        if (backStack.isEmpty()) return
        if (destination !in backStack) return

        while (backStack.isNotEmpty() && backStack.last() != destination) {
            backStack.removeLastOrNull()
        }
    }
}