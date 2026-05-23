package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.EaseInOutCubic
import cn.yajienet.huanaer.ui.theme.LocalAppShapes

@Composable
fun PillFilter(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    val shapes = LocalAppShapes.current

    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            selectedColor.copy(alpha = 0.15f)
        } else {
            unselectedColor.copy(alpha = 0.5f)
        },
        animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic),
        label = "pill_container_color"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            selectedColor.copy(alpha = 0.4f)
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        },
        animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic),
        label = "pill_border_color"
    )

    val borderWidth = if (selected) 1.dp else 0.5.dp

    Surface(
        modifier = modifier,
        shape = shapes.pill,
        color = containerColor,
        border = BorderStroke(borderWidth, borderColor),
        onClick = onClick,
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    )
}