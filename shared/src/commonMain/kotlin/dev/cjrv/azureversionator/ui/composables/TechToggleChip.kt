package dev.cjrv.azureversionator.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.cjrv.azureversionator.theme.CornerRadius

@Composable
fun TechToggleChip(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)

    Surface(
        modifier = modifier.toggleable(
            value = checked,
            role = Role.Checkbox,
            onValueChange = onCheckedChange
        ),
        shape = RoundedCornerShape(CornerRadius),
        color = if (checked) {
            activeColor.copy(alpha = 0.14f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (checked) activeColor else inactiveColor
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(16.dp),
                shape = RoundedCornerShape(3.dp),
                color = if (checked) activeColor else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (checked) activeColor else inactiveColor
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (checked) {
                        TechLabel(
                            text = "✓",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            TechLabel(
                text = label,
                modifier = Modifier.padding(start = 8.dp),
                color = if (checked) activeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
