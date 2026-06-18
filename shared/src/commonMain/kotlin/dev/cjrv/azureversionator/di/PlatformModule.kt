package dev.cjrv.azureversionator.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin module.
 * Each target provides its own implementation via `actual fun`.
 *
 * Add platform-specific dependencies here (e.g. Android Context,
 * platform dispatchers, file system paths, native SDKs).
 */
expect fun platformModule(): Module

