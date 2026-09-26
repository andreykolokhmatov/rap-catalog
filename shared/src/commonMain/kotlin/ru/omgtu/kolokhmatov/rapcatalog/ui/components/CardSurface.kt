package ru.omgtu.kolokhmatov.rapcatalog.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardSurface(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val color by animateColorAsState(
        if (hovered || focused) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
                .compositeOverPrimary(MaterialTheme.colorScheme.primary)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
    )
    val border = when {
        focused -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        hovered -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        else -> null
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
        shape = RoundedCornerShape(14.dp),
        color = color,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = border,
        content = content,
    )
}

private fun Color.compositeOverPrimary(primary: Color): Color = Color(
    red = red * 0.88f + primary.red * 0.12f,
    green = green * 0.88f + primary.green * 0.12f,
    blue = blue * 0.88f + primary.blue * 0.12f,
    alpha = 1f,
)
