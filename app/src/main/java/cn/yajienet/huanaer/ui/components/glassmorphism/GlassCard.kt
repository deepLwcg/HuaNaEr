package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.LocalAppShapes

/**
 * 毛玻璃风格卡片
 * 柔光弥散风格：半透明背景 + 极细白色描边 + 微弱内部渐变
 *
 * @param glassAlpha 透明度（浅色模式默认 0.75f）
 * @param borderAlpha 描边透明度（浅色模式默认 0.3f）
 * @param blurRadius 模糊半径（视觉模拟，不实际 blur）
 * @param content 卡片内容
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    glassAlpha: Float = 0.75f,
    borderAlpha: Float = 0.3f,
    blurRadius: Dp = 20.dp,
    tonalElevation: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shapes = LocalAppShapes.current
    val colorScheme = MaterialTheme.colorScheme

    // 计算颜色
    val isDark = colorScheme.background == Color(0xFF111318) ||
                 colorScheme.background == Color(0xFF12141A) ||
                 colorScheme.surface.luminance() < 0.5f

    val containerColor = remember(isDark, colorScheme, glassAlpha) {
        if (isDark) {
            // 深色模式：半透明深灰
            colorScheme.surfaceContainer.copy(alpha = 0.60f * glassAlpha)
        } else {
            // 浅色模式：半透明白/奶油色
            colorScheme.surfaceContainerHigh.copy(alpha = glassAlpha)
        }
    }

    val borderColor = remember(isDark, borderAlpha) {
        if (isDark) {
            Color.White.copy(alpha = 0.15f * borderAlpha)
        } else {
            Color.White.copy(alpha = borderAlpha)
        }
    }

    // 微弱内部渐变颜色（模拟光照效果）
    val gradientColorTop = remember(isDark, colorScheme) {
        if (isDark) {
            colorScheme.surfaceTint.copy(alpha = 0.05f)
        } else {
            Color.White.copy(alpha = 0.08f)
        }
    }

    Surface(
        modifier = modifier.drawBehind {
            // 内顶部微弱渐变（模拟模糊光照效果）
            val gradientHeight = blurRadius.toPx() * 0.5f
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientColorTop, Color.Transparent),
                    startY = 0f,
                    endY = gradientHeight
                )
            )
        },
        shape = shapes.cardLarge,
        color = containerColor,
        border = BorderStroke(0.5.dp, borderColor),
        tonalElevation = tonalElevation,
        content = {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    )
}

/**
 * 检测颜色亮度
 */
private fun Color.luminance(): Float {
    // 简化的亮度计算
    return (red * 0.299f + green * 0.587f + blue * 0.114f)
}