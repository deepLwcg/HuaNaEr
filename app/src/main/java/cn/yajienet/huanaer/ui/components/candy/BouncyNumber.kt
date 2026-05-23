package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.animation.core.Animatable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import cn.yajienet.huanaer.ui.theme.numberRollTween
import cn.yajienet.huanaer.util.CurrencyFormat
import kotlin.math.abs

@Composable
fun BouncyNumber(
    targetValue: Double,
    modifier: Modifier = Modifier,
    prefix: String = "¥",
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.displayMedium
) {
    val targetCents = (targetValue * 100).toInt()
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(targetCents) {
        animatable.animateTo(
            targetValue = targetCents.toFloat(),
            animationSpec = numberRollTween()
        )
    }

    val displayValue = animatable.value.toInt() / 100.0

    Text(
        text = prefix + CurrencyFormat.formatWithoutSymbol(abs(displayValue)),
        style = style,
        color = color,
        modifier = modifier
    )
}
