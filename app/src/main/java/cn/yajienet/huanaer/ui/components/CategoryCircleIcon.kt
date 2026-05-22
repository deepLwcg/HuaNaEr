package cn.yajienet.huanaer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A circular icon component displaying category with its color
 */
@Composable
fun CategoryCircleIcon(
    name: String,
    color: String,
    size: Dp = 48.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    modifier: Modifier = Modifier
) {
    val backgroundColor = remember(color) {
        try { Color(android.graphics.Color.parseColor(color)) }
        catch (_: IllegalArgumentException) { Color(0xFF808080) }
    }
    val luminance = 0.299f * backgroundColor.red + 0.587f * backgroundColor.green + 0.114f * backgroundColor.blue
    val textColor = if (luminance > 0.5f) Color(0xFF1A1A1A) else Color.White

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(2),
            color = textColor,
            style = textStyle
        )
    }
}