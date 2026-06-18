package dev.cjrv.azureversionator.di

import org.koin.dsl.module

/**
 * Root Koin module. Add sub-modules here as features grow.
 */
val appModule = module {
    includes(platformModule())
    // Feature modules will be included here
}
