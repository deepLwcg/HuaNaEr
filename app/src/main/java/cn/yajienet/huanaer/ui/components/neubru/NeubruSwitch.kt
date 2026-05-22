package cn.yajienet.huanaer.ui.components.neubru

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NeubruSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 26.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "switchThumbOffset"
    )

    val trackColor = if (checked) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant
    val thumbColor = if (checked) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.outline

    Surface(
        modifier = modifier.size(width = 52.dp, height = 28.dp),
        shape = RoundedCornerShape(14.dp),
        color = trackColor,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
        onClick = { onCheckedChange(!checked) }
    ) {
        Box(modifier = Modifier.offset(x = thumbOffset, y = 2.dp)) {
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = thumbColor,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                content = {}
            )
        }
    }
}