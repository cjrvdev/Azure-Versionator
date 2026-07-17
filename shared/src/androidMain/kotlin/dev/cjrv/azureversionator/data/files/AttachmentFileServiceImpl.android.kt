package dev.cjrv.azureversionator.data.files

import android.content.Context
import android.os.Environment
import java.io.File

class AttachmentFileServiceImpl(
    private val context: Context
) : AttachmentFileService {
    override fun defaultDownloadDirectoryPath(): String {
        val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        return (downloadsDir ?: context.filesDir).absolutePath
    }

    override fun saveFiles(directoryPath: String, files: List<AttachmentFilePayload>): Result<Int> = runCatching {
        val targetDir = File(directoryPath.trim())
        ensureDirectory(targetDir)

        files.forEach { file ->
            val destination = uniqueFile(targetDir, sanitizeFileName(file.fileName))
            destination.writeBytes(file.content)
        }

        files.size
    }

    private fun ensureDirectory(directory: File) {
        if (!directory.exists()) {
            check(directory.mkdirs()) {
                "Could not create destination directory: ${directory.absolutePath}"
            }
        }
        check(directory.isDirectory) {
            "Destination path is not a directory: ${directory.absolutePath}"
        }
    }

    private fun uniqueFile(directory: File, fileName: String): File {
        val sanitizedName = fileName.ifBlank { "attachment.bin" }
        var candidate = File(directory, sanitizedName)
        if (!candidate.exists()) return candidate

        val extensionStart = sanitizedName.lastIndexOf('.')
        val baseName = if (extensionStart > 0) {
            sanitizedName.substring(0, extensionStart)
        } else {
            sanitizedName
        }
        val extension = if (extensionStart > 0 && extensionStart < sanitizedName.lastIndex) {
            sanitizedName.substring(extensionStart)
        } else {
            ""
        }

        var index = 1
        while (candidate.exists()) {
            candidate = File(directory, "$baseName ($index)$extension")
            index++
        }

        return candidate
    }

    private fun sanitizeFileName(name: String): String {
        val invalidChars = setOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')
        return buildString {
            name.forEach { c ->
                append(if (c in invalidChars || c.code < 32) '_' else c)
            }
        }.trim().ifBlank { "attachment.bin" }
    }
}
