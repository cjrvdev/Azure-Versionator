package dev.cjrv.azureversionator.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.appicon
import org.jetbrains.compose.resources.painterResource

@Composable
fun appIconPainter(): Painter = painterResource(Res.drawable.appicon)

