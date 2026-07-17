package dev.cjrv.azureversionator.data.files

data class AttachmentFilePayload(
    val fileName: String,
    val content: ByteArray
)

interface AttachmentFileService {
    fun defaultDownloadDirectoryPath(): String
    fun saveFiles(directoryPath: String, files: List<AttachmentFilePayload>): Result<Int>
}
