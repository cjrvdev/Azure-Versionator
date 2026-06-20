package dev.cjrv.azureversionator.di

import org.koin.dsl.KoinConfiguration

fun createKoinConfiguration(): KoinConfiguration =
    KoinConfiguration {
        modules(
            dataModule,
            //domainModule,
            viewModelModule,
            navigationModule,
            platformModule()
        )
    }