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
    // 缓存解析后的颜色，避免每次重组都解析颜色字符串
    val backgroundColor = remember(color) { Color(android.graphics.Color.parseColor(color)) }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(2),
            color = Color.White,
            style = textStyle
        )
    }
}