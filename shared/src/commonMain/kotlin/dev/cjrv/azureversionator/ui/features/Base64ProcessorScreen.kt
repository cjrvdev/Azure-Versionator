package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.base64_copy_result
import azureversionator.shared.generated.resources.base64_decode
import azureversionator.shared.generated.resources.base64_encode
import azureversionator.shared.generated.resources.base64_encode_decode
import azureversionator.shared.generated.resources.base64_image_input_placeholder
import azureversionator.shared.generated.resources.base64_image_mode
import azureversionator.shared.generated.resources.base64_input
import azureversionator.shared.generated.resources.base64_result
import azureversionator.shared.generated.resources.base64_save_image
import azureversionator.shared.generated.resources.base64_select_image
import azureversionator.shared.generated.resources.base64_text_mode
import azureversionator.shared.generated.resources.copy
import azureversionator.shared.generated.resources.download
import azureversionator.shared.generated.resources.error
import azureversionator.shared.generated.resources.ok
import azureversionator.shared.generated.resources.success
import azureversionator.shared.generated.resources.upload
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomMultilineTextField
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.CustomSecondaryButton
import dev.cjrv.azureversionator.ui.composables.TechLabel
import dev.cjrv.azureversionator.ui.composables.TechPanel
import dev.cjrv.azureversionator.ui.composables.TechToggleChip
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Base64ProcessorScreen(onNavigateBack: () -> Unit) {
    val vm = koinViewModel<Base64ProcessorViewModel>()
    val state by vm.state.collectAsState()
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    state.errorMessage?.let { message ->
        ProcessorMessageDialog(
            title = stringResource(Res.string.error),
            message = message,
            onDismiss = vm::onMessageConsumed
        )
    }
    state.successMessage?.let { message ->
        ProcessorMessageDialog(
            title = stringResource(Res.string.success),
            message = message,
            onDismiss = vm::onMessageConsumed
        )
    }

    Screen {
        Scaffold(
            topBar = {
                TopAppBar(
                    stringResource(Res.string.base64_encode_decode),
                    hasBackButton = true,
                    onBackPressed = onNavigateBack
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                TechPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MarginMedium)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MarginMedium)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(MarginMedium)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(MarginMedium)) {
                            TechToggleChip(
                                label = stringResource(Res.string.base64_text_mode),
                                checked = state.mode == Base64ProcessorViewModel.ProcessorMode.Text,
                                onCheckedChange = {
                                    vm.onModeSelected(Base64ProcessorViewModel.ProcessorMode.Text)
                                }
                            )
                            TechToggleChip(
                                label = stringResource(Res.string.base64_image_mode),
                                checked = state.mode == Base64ProcessorViewModel.ProcessorMode.Image,
                                onCheckedChange = {
                                    vm.onModeSelected(Base64ProcessorViewModel.ProcessorMode.Image)
                                }
                            )
                        }

                        when (state.mode) {
                            Base64ProcessorViewModel.ProcessorMode.Text -> TextProcessor(
                                state = state,
                                onInputChanged = vm::onTextInputChanged,
                                onEncode = vm::encodeText,
                                onDecode = vm::decodeText,
                                onCopy = {
                                    coroutineScope.launch {
                                        clipboard.setClipEntry(createPlainTextClipEntry(state.textResult))
                                    }
                                }
                            )
                            Base64ProcessorViewModel.ProcessorMode.Image -> ImageProcessor(
                                state = state,
                                onInputChanged = vm::onImageInputChanged,
                                onSelectImage = vm::selectImage,
                                onSaveImage = vm::saveImage,
                                onCopy = {
                                    coroutineScope.launch {
                                        clipboard.setClipEntry(createPlainTextClipEntry(state.imageResult))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextProcessor(
    state: Base64ProcessorViewModel.UIState,
    onInputChanged: (String) -> Unit,
    onEncode: () -> Unit,
    onDecode: () -> Unit,
    onCopy: () -> Unit
) {
    CustomMultilineTextField(
        label = stringResource(Res.string.base64_input),
        value = state.textInput,
        onValueChange = onInputChanged,
        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 200.dp)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(MarginMedium)) {
        CustomPrimaryButton(
            text = stringResource(Res.string.base64_encode),
            onClick = onEncode,
            enabled = state.textInput.isNotBlank(),
            modifier = Modifier.weight(1f)
        )
        CustomSecondaryButton(
            text = stringResource(Res.string.base64_decode),
            onClick = onDecode,
            enabled = state.isTextInputBase64,
            modifier = Modifier.weight(1f)
        )
    }
    ResultField(value = state.textResult, onCopy = onCopy)
}

@Composable
private fun ImageProcessor(
    state: Base64ProcessorViewModel.UIState,
    onInputChanged: (String) -> Unit,
    onSelectImage: () -> Unit,
    onSaveImage: () -> Unit,
    onCopy: () -> Unit
) {
    CustomMultilineTextField(
        label = stringResource(Res.string.base64_input),
        value = state.imageInput,
        onValueChange = onInputChanged,
        placeholder = stringResource(Res.string.base64_image_input_placeholder),
        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 200.dp)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(MarginMedium)) {
        CustomPrimaryButton(
            text = stringResource(Res.string.base64_select_image),
            onClick = onSelectImage,
            modifier = Modifier.weight(1f),
            leadingIcon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.upload),
                    contentDescription = null
                )
            }
        )
        CustomSecondaryButton(
            text = stringResource(Res.string.base64_save_image),
            onClick = onSaveImage,
            modifier = Modifier.weight(1f),
            leadingIcon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.download),
                    contentDescription = null
                )
            }
        )
    }
    ResultField(value = state.imageResult, onCopy = onCopy)
}

@Composable
private fun ResultField(value: String, onCopy: () -> Unit) {
    CustomMultilineTextField(
        label = stringResource(Res.string.base64_result),
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 200.dp)
    )
    Box(modifier = Modifier.fillMaxWidth()) {
        CustomSecondaryButton(
            text = stringResource(Res.string.base64_copy_result),
            enabled = value.isNotBlank(),
            onClick = onCopy,
            modifier = Modifier.align(Alignment.CenterEnd),
            leadingIcon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.copy),
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun ProcessorMessageDialog(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { TechLabel(text = title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                TechLabel(text = stringResource(Res.string.ok))
            }
        }
    )
}
