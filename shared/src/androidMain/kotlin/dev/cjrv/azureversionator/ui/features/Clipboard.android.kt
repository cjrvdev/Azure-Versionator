package dev.cjrv.azureversionator.ui.features

import android.content.ClipData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

actual fun createPlainTextClipEntry(text: String): ClipEntry =
    ClipEntry(ClipData.newPlainText(null, text))
