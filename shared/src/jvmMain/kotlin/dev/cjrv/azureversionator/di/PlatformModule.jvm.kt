package dev.cjrv.azureversionator.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Desktop (JVM) specific Koin bindings.
 *
 * Add JVM/Desktop-only dependencies here, for example:
 *   single { java.util.prefs.Preferences.userRoot() }
 */
actual fun platformModule(): Module = module {
    // Desktop-specific bindings go here
}

