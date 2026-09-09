package dev.cjrv.azureversionator.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Base sealed interface for all navigation destinations in the app.
 *
 * WHY a sealed interface instead of a base class?
 * - Sealed interfaces allow a route to implement multiple interfaces (e.g. Screen + BottomBarItem)
 *   which is impossible with a sealed class hierarchy.
 * - They compose better in multi-module setups: each feature module can define its own
 *   sealed interface that extends Screen, and still be used in a single NavDisplay.
 * - They are pure route descriptors — no behavior — keeping navigation definitions declarative.
 *
 * Usage:
 *   @Serializable data object HomeScreen : Screen
 *   @Serializable data class DetailScreen(val id: String) : Screen
 */

@Serializable
sealed interface Route : NavKey

/** Top-level destinations of the app. */
@Serializable
data object Home : Route

@Serializable
data object NewVersion : Route

@Serializable
data object AttachmentBulkDownloader : Route

@Serializable
data object EditProfile : Route

