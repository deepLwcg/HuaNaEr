package cn.yajienet.huanaer.ui.components.neubru

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    val targetCents = (targetValue * 100).roundToLong()
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(targetCents) {
        animatable.animateTo(
            targetValue = targetCents.toFloat(),
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
    }

    val displayValue = animatable.value.roundToLong().toDouble() / 100.0

    Text(
        text = "$prefix${CurrencyFormat.format(displayValue)}",
        style = style,
        color = color,
        modifier = modifier
    )
}