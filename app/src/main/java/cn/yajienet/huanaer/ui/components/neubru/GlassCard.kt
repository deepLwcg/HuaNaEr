package cn.yajienet.huanaer.ui.components.neubru

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val borderAlpha = if (isDark) 0.15f else 0.3f
    val borderColor = if (isDark) MaterialTheme.colorScheme.surfaceTint.copy(alpha = borderAlpha)
        else Color.White.copy(alpha = borderAlpha)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 2.dp
    ) {
        content()
    }
}