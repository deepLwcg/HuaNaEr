package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.LocalAppShapes

/**
 * 骨架屏占位组件
 * 柔光弥散风格：更柔和的渐变色，更缓慢的动画
 *
 * 与 ShimmerPlaceholder 的区别：
 * - 渐变色 alpha 更低（0.4 → 0.25）
 * - 动画时长从 1000ms → 1500ms
 *
 * @param modifier 修饰符
 * @param shape 形状（默认圆角矩形）
 * @param width 宽度
 * @param height 高度
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = LocalAppShapes.current.cardSmall,
    width: Dp? = null,
    height: Dp = 16.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing)
        ),
        label = "shimmer_offset"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.25f),
        Color.LightGray.copy(alpha = 0.10f),
        Color.LightGray.copy(alpha = 0.25f)
    )

    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(shimmerOffset * 100, 0f),
        end = androidx.compose.ui.geometry.Offset(shimmerOffset * 100 + 200f, 0f)
    )

    Box(
        modifier = modifier
            .then(if (width != null) Modifier.fillMaxWidth() else Modifier)
            .height(height)
            .clip(shape)
            .background(shimmerBrush)
    )
}

@Composable
fun ShimmerLine(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = 12.dp
) = ShimmerBox(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    width = width,
    height = height
)

@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing)
        ),
        label = "shimmer_offset"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.25f),
        Color.LightGray.copy(alpha = 0.10f),
        Color.LightGray.copy(alpha = 0.25f)
    )

    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(shimmerOffset * 100, 0f),
        end = androidx.compose.ui.geometry.Offset(shimmerOffset * 100 + 200f, 0f)
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush)
    )
}

@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    height: Dp = 80.dp
) = ShimmerBox(
    modifier = modifier.fillMaxWidth(),
    shape = LocalAppShapes.current.cardMedium,
    height = height
)