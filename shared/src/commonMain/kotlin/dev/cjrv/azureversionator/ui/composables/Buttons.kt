package dev.cjrv.azureversionator.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.light_mode
import dev.cjrv.azureversionator.theme.AzureVersionatorTheme
import dev.cjrv.azureversionator.theme.CornerRadius
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun CustomPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    PrimaryButton(
        text = text,
        modifier = modifier,
        style = regularButtonStyle(),
        enabled = enabled,
        leadingIcon = leadingIcon,
        onClick = onClick
    )
}

@Composable
fun CustomPrimaryCompactButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    PrimaryButton(
        text = text,
        modifier = modifier,
        style = compactButtonStyle(),
        enabled = enabled,
        leadingIcon = leadingIcon,
        onClick = onClick
    )
}

@Composable
private fun PrimaryButton(
    text: String,
    modifier: Modifier,
    style: ButtonStyle,
    enabled: Boolean,
    leadingIcon: (@Composable () -> Unit)?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = style.minHeight),
        shape = RoundedCornerShape(CornerRadius),
        contentPadding = style.contentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            TechLabel(
                text = text,
                color = LocalContentColor.current,
                style = style.textStyle
            )
        }
    }
}

@Composable
fun CustomSecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    SecondaryButton(
        text = text,
        modifier = modifier,
        style = regularButtonStyle(),
        enabled = enabled,
        leadingIcon = leadingIcon,
        onClick = onClick
    )
}

@Composable
fun CustomSecondaryCompactButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    SecondaryButton(
        text = text,
        modifier = modifier,
        style = compactButtonStyle(),
        enabled = enabled,
        leadingIcon = leadingIcon,
        onClick = onClick
    )
}

@Composable
private fun SecondaryButton(
    text: String,
    modifier: Modifier,
    style: ButtonStyle,
    enabled: Boolean,
    leadingIcon: (@Composable () -> Unit)?,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = style.minHeight),
        shape = RoundedCornerShape(CornerRadius),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            }
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        contentPadding = style.contentPadding
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            TechLabel(
                text = text,
                color = LocalContentColor.current,
                style = style.textStyle
            )
        }
    }
}

@Composable
fun TechActionButton(
    icon: DrawableResource,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(34.dp),
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = vectorResource(icon),
                contentDescription = contentDescription,
                modifier = Modifier.size(18.dp),
                tint = tint
            )
        }
    }
}

private data class ButtonStyle(
    val minHeight: Dp,
    val contentPadding: PaddingValues,
    val textStyle: TextStyle
)

@Composable
private fun compactButtonStyle(): ButtonStyle {
    return ButtonStyle(
        minHeight = 40.dp,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        textStyle = MaterialTheme.typography.labelMedium
    )
}

@Composable
private fun regularButtonStyle(): ButtonStyle {
    return ButtonStyle(
        minHeight = 48.dp,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        textStyle = MaterialTheme.typography.labelLarge
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonsPreview() {
    AzureVersionatorTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(16.dp)) {
            CustomSecondaryButton(text = "Secondary Regular")
            CustomSecondaryCompactButton(text = "Secondary Compact")
            CustomSecondaryCompactButton(text = "Secondary Compact disabled", enabled = false)
            CustomPrimaryButton(
                text = "Primary Regular",
            )
            CustomPrimaryCompactButton(text = "Primary Compact")
            CustomSecondaryButton(text = "Secondary Disabled", enabled = false)
            CustomPrimaryButton(
                text = "Primary Disabled",
                enabled = false
            )
            TechActionButton(
                icon = Res.drawable.light_mode,
                contentDescription = "Light Mode",
                onClick = {}
            )
        }
    }
}
