package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import cn.yajienet.huanaer.ui.theme.EaseInOutCubic
import cn.yajienet.huanaer.util.CurrencyFormat
import kotlin.math.abs

/**
 * 数字滚动动画
 * 柔光弥散风格：舒缓的缓动函数（EaseInOutCubic）
 *
 * 与 AnimatedCounter 的区别：
 * - 动画时长从 400ms → 500ms
 * - 缓动从 FastOutSlowInEasing → EaseInOutCubic
 *
 * @param targetValue 目标值
 * @param prefix 前缀（默认 "¥"）
 * @param color 文字颜色
 * @param style 文字样式
 */
@Composable
fun AnimatedNumber(
    targetValue: Double,
    modifier: Modifier = Modifier,
    prefix: String = "¥",
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.displayMedium
) {
    // 使用分（cents）为单位以保证精度
    val targetCents = (targetValue * 100).toInt()
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(targetCents) {
        animatable.animateTo(
            targetValue = targetCents.toFloat(),
            animationSpec = tween(durationMillis = 500, easing = EaseInOutCubic)
        )
    }

    val currentValue = animatable.value.toInt()
    val displayValue = currentValue / 100.0

    Text(
        text = prefix + CurrencyFormat.format(abs(displayValue)),
        style = style,
        color = color,
        modifier = modifier
    )
}