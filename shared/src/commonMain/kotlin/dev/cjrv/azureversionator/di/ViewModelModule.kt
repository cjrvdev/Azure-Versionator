package dev.cjrv.azureversionator.di

import dev.cjrv.azureversionator.ui.features.AttachmentBulkDownloaderViewModel
import dev.cjrv.azureversionator.ui.features.Base64ProcessorViewModel
import dev.cjrv.azureversionator.ui.features.EditProfileViewModel
import dev.cjrv.azureversionator.ui.features.HomeViewModel
import dev.cjrv.azureversionator.ui.features.MergeBranchesViewModel
import dev.cjrv.azureversionator.ui.features.NewVersionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::NewVersionViewModel)
    viewModelOf(::AttachmentBulkDownloaderViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::Base64ProcessorViewModel)
    viewModelOf(::MergeBranchesViewModel)
}