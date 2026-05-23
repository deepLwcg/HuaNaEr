package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 轻拟物风格开关
 */
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    thumbColor: Color = if (checked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
    trackColor: Color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
    borderColor: Color = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 26.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "thumb_offset"
    )

    Surface(
        modifier = modifier.size(52.dp, 28.dp),
        shape = RoundedCornerShape(14.dp),
        color = trackColor,
        border = BorderStroke(0.5.dp, borderColor),
        tonalElevation = 1.dp,
        onClick = { onCheckedChange(!checked) },
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset, top = 2.dp)
                .size(24.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = thumbColor,
                border = BorderStroke(0.5.dp, borderColor),
                modifier = Modifier.size(24.dp)
            ) {}
        }
    }
}