package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.LocalAppShapes

/**
 * 轻拟物风格卡片
 * 柔光弥散风格：柔和 elevation + 极细描边
 *
 * @param containerColor 背景色
 * @param borderColor 描边色
 * @param cornerRadius 圆角
 * @param elevation 卡片阴影高度
 * @param content 卡片内容
 */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    borderColor: Color? = null,
    cornerRadius: Dp? = null,
    elevation: Dp = 2.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shapes = LocalAppShapes.current
    val colorScheme = MaterialTheme.colorScheme

    val isDark = colorScheme.surface.luminance() < 0.5f

    val actualBorderColor = remember(borderColor, isDark) {
        borderColor ?: if (isDark) {
            Color.Transparent
        } else {
            Color.White.copy(alpha = 0.2f)
        }
    }

    val shape = remember(cornerRadius, shapes) {
        if (cornerRadius != null) {
            androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius)
        } else {
            shapes.cardMedium
        }
    }

    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        border = if (actualBorderColor == Color.Transparent) null else BorderStroke(0.5.dp, actualBorderColor),
        content = {
            Box(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    )
}

private fun Color.luminance(): Float {
    return (red * 0.299f + green * 0.587f + blue * 0.114f)
}