package dev.cjrv.azureversionator.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.cjrv.azureversionator.theme.BorderAlpha
import dev.cjrv.azureversionator.theme.CornerRadius
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Technical label styled Text. 
 * Automatically applies uppercase, bold weight, and letter spacing.
 */
@Composable
fun TechLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.labelSmall
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = color,
        style = style.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    )
}

/**
 * Standard Tech Section or Screen Title.
 */
@Composable
fun TechTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = color,
        style = style.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    )
}

/**
 * Standard container for tech elements with consistent border and radius.
 */
@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = BorderAlpha),
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(CornerRadius),
        border = BorderStroke(borderWidth, borderColor),
        content = content
    )
}

/**
 * Higher contrast container for interactive elements.
 */
@Composable
fun TechPanel(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = BorderAlpha),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(CornerRadius),
        border = BorderStroke(1.dp, borderColor),
        content = content
    )
}

/**
 * Standard Tech Icon container.
 */
@Composable
fun TechIcon(
    icon: DrawableResource,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = tint.copy(alpha = 0.1f)
) {
    Box(
        modifier = modifier
            .size(size * 2)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = vectorResource(icon),
            contentDescription = null,
            modifier = Modifier.size(size),
            tint = tint
        )
    }
}
