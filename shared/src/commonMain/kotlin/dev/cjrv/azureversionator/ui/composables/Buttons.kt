package dev.cjrv.azureversionator.ui.composables

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
fun CustomButton(text: String, onClick: () -> Unit = {}) {
    Button(onClick = onClick) {
        Text(text = text)
    }
}

@Preview
@Composable
fun ButtonsPreview(@PreviewParameter(ButtonsPreviewParameterProvider::class) text: String) {
    CustomButton(text)
}

private class ButtonsPreviewParameterProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String>
        get() = sequenceOf("This is a button")
}