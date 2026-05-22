package cn.yajienet.huanaer.ui.components.neubru

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import cn.yajienet.huanaer.util.CurrencyFormat
import kotlin.math.roundToLong

@Composable
fun AnimatedCounter(
    targetValue: Double,
    modifier: Modifier = Modifier,
    prefix: String = "¥",
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.headlineLarge
) {
    // 以分为单位做动画避免 Float 精度丢失（最大支持约 2.1 亿元）
    val targetCents = (targetValue * 100).roundToLong()
    val animatedCents by animateFloatAsState(
        targetValue = targetCents.toFloat(),
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "animatedCounter"
    )
    val displayValue = (animatedCents.toLong()).toDouble() / 100.0

    Text(
        text = "$prefix${CurrencyFormat.format(displayValue)}",
        style = style,
        color = color,
        modifier = modifier
    )
}