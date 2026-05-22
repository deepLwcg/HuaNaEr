package cn.yajienet.huanaer.ui.components.neubru

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    width: Dp = 200.dp,
    height: Dp = 20.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = InfiniteRepeatableSpec(tween(1000)),
        label = "shimmerOffset"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    )

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.linearGradient(
                        colors = shimmerColors,
                        start = Offset(shimmerOffset * size.width, 0f),
                        end = Offset((shimmerOffset + 1f) * size.width, size.height)
                    ),
                    size = size
                )
            }
    )
}

@Composable
fun ShimmerLine(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 16.dp
) {
    ShimmerPlaceholder(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        width = width,
        height = height
    )
}

@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    ShimmerPlaceholder(
        modifier = modifier,
        shape = CircleShape,
        width = size,
        height = size
    )
}

@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    ShimmerPlaceholder(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        width = 300.dp,
        height = height
    )
}