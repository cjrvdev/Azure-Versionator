package dev.cjrv.azureversionator.data.profiles

import dev.cjrv.azureversionator.data.model.app.SharedProfile
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.writeString
import kotlinx.serialization.json.Json

class ProfileTransferService(
    private val settingsRepository: AzureSettingsRepository
) {
    suspend fun exportActiveProfile(): ProfileTransferResult {
        return try {
            val profile = settingsRepository.getActiveProfile()
            val file = FileKit.openFileSaver(
                suggestedName = profile.name.toFileName(),
                defaultExtension = "json",
                allowedExtensions = setOf("json")
            ) ?: return ProfileTransferResult.Cancelled

            file.writeString(json.encodeToString(SharedProfile.fromProfile(profile)))
            ProfileTransferResult.Exported
        } catch (exception: Exception) {
            ProfileTransferResult.Failed(exception)
        }
    }

    suspend fun importProfile(): ProfileTransferResult {
        return try {
            val file = FileKit.openFilePicker(FileKitType.File("json"))
                ?: return ProfileTransferResult.Cancelled
            val sharedProfile = json.decodeFromString<SharedProfile>(file.readBytes().decodeToString())
            val importedProfile = sharedProfile.toProfile()

            settingsRepository.saveProfile(importedProfile)
            settingsRepository.setActiveProfile(importedProfile)
            ProfileTransferResult.Imported
        } catch (exception: Exception) {
            ProfileTransferResult.Failed(exception)
        }
    }

    private fun String.toFileName(): String =
        lowercase()
            .replace(Regex("[^a-z0-9-_]+"), "-")
            .trim('-')
            .ifEmpty { "azure-versionator-profile" }

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
}

sealed interface ProfileTransferResult {
    data object Cancelled : ProfileTransferResult
    data object Exported : ProfileTransferResult
    data object Imported : ProfileTransferResult
    data class Failed(val exception: Exception) : ProfileTransferResult
}
