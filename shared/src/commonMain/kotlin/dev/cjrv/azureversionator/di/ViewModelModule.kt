package dev.cjrv.azureversionator.di

import dev.cjrv.azureversionator.ui.features.HomeViewModel
import dev.cjrv.azureversionator.ui.features.NewVersionViewModel
import dev.cjrv.azureversionator.ui.features.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::NewVersionViewModel)
}