package dev.cjrv.azureversionator.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.arrow_drop_down
import azureversionator.shared.generated.resources.arrow_drop_up
import azureversionator.shared.generated.resources.collapse
import azureversionator.shared.generated.resources.expand
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ExpandableSection(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ExpandableSectionTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                isExpanded = isExpanded,
                title = title
            )

            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = isExpanded
            ) {
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ExpandableSectionTitle(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    title: @Composable () -> Unit
) {
    val icon = if (isExpanded) 
        vectorResource(Res.drawable.arrow_drop_up) 
    else 
        vectorResource(Res.drawable.arrow_drop_down)

    Row(
        modifier = modifier.padding(vertical = 12.dp, horizontal = 8.dp), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            imageVector = icon,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            contentDescription = if (isExpanded) stringResource(Res.string.collapse) else stringResource(Res.string.expand)
        )
        title()
    }
}
