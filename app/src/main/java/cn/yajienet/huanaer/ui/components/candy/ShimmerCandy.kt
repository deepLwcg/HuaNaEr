package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.CandyPink
import cn.yajienet.huanaer.ui.theme.CandyPurple
import cn.yajienet.huanaer.ui.theme.CandyYellow
import cn.yajienet.huanaer.ui.theme.LocalAppShapes

@Composable
fun ShimmerCandy(
    modifier: Modifier = Modifier,
    shape: Shape = LocalAppShapes.current.cardMedium
) {
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Restart),
        label = "shimmer_offset"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            CandyPink.copy(alpha = 0.15f),
            CandyYellow.copy(alpha = 0.25f),
            CandyPurple.copy(alpha = 0.15f),
            CandyPink.copy(alpha = 0.15f)
        ),
        start = Offset(offset - 200f, 0f),
        end = Offset(offset, 100f)
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun ShimmerCandyLine(modifier: Modifier = Modifier, height: Dp = 16.dp) {
    ShimmerCandy(modifier = modifier.size(height = height, width = 120.dp))
}

@Composable
fun ShimmerCandyCircle(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    ShimmerCandy(modifier = modifier.size(size), shape = LocalAppShapes.current.fab)
}

@Composable
fun ShimmerCandyCard(modifier: Modifier = Modifier) {
    ShimmerCandy(
        modifier = modifier.fillMaxWidth().height(100.dp),
        shape = LocalAppShapes.current.cardLarge
    )
}
