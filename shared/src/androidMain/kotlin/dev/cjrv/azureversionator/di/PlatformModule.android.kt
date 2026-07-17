package dev.cjrv.azureversionator.di

import dev.cjrv.azureversionator.data.files.AttachmentFileService
import dev.cjrv.azureversionator.data.files.AttachmentFileServiceImpl
import dev.cjrv.azureversionator.data.openurl.OpenUrlService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific Koin bindings.
 *
 * Android Context is automatically provided by Koin 4's KoinApplication composable
 * and is available here via `get<Context>()` or `androidContext()` helpers.
 *
 * Add Android-only dependencies here, for example:
 *   single { get<Context>().getSharedPreferences("prefs", Context.MODE_PRIVATE) }
 */
actual fun platformModule(): Module = module {
    single<OpenUrlService> { OpenUrlService(get()) }
    single<AttachmentFileService> { AttachmentFileServiceImpl(get()) }
}
