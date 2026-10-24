package dev.cjrv.azureversionator.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.cjrv.azureversionator.theme.BorderAlpha
import dev.cjrv.azureversionator.theme.CornerRadius
import dev.cjrv.azureversionator.theme.LocalDarkTheme
import dev.cjrv.azureversionator.theme.MarginMedium
import org.jetbrains.compose.resources.DrawableResource


@Composable
fun OperationCard(
    title: String,
    subtitle: String,
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val highlighted = hovered || focused
    val colors = MaterialTheme.colorScheme
    val backgroundColor by animateColorAsState(
        targetValue = if (highlighted) {
            lerp(colors.surfaceContainerHigh, colors.primary, 0.06f)
        } else {
            colors.surfaceContainerHigh
        }
    )
    val borderColor by animateColorAsState(
        targetValue = if (highlighted) {
            colors.primary.copy(alpha = 0.65f)
        } else {
            colors.outline.copy(alpha = BorderAlpha)
        }
    )
    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius),
        border = BorderStroke(1.dp, borderColor),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TechIcon(icon = icon)
            Spacer(modifier = Modifier.width(MarginMedium))
            Column {
                TechLabel(
                    text = subtitle,
                    color = if (LocalDarkTheme.current) {
                        colors.primary.copy(alpha = 0.85f)
                    } else {
                        colors.tertiary
                    }
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}