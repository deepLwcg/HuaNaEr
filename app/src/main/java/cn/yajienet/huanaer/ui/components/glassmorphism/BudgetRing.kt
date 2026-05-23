package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 环形进度指示器
 * 柔光弥散风格：柔和背景环 + 进度环末端微弱弥散光
 *
 * 与 RingProgressIndicator 的区别：
 * - 背景环 alpha 从 1.0 → 0.3f
 * - 环宽从 6dp → 8dp
 * - 动画从 tween(800) → spring(dampingRatio=0.65)
 * - 进度弧末端微弱弥散光效果
 *
 * @param progress 进度值（0-1）
 * @param color 进度环颜色
 * @param size 环尺寸
 * @param strokeWidth 环宽度
 */
@Composable
fun BudgetRing(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.3f),
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = 200f
        ),
        label = "ring_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val radius = (size.toPx() - strokePx) / 2f
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

            // 背景环（柔和）
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokePx)
            )

            // 进度环（末端微弱弥散光效果）
            val sweepAngle = animatedProgress * 360f
            if (sweepAngle > 0f) {
                // 弥散光效果（进度弧末端）
                val endAngleRad = (sweepAngle - 90f) * kotlin.math.PI / 180f
                val glowX = center.x + radius * kotlin.math.cos(endAngleRad).toFloat()
                val glowY = center.y + radius * kotlin.math.sin(endAngleRad).toFloat()

                drawCircle(
                    color = color.copy(alpha = 0.2f),
                    radius = strokePx * 1.5f,
                    center = Offset(glowX, glowY)
                )

                // 进度弧
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = strokePx,
                        cap = StrokeCap.Round
                    ),
                    topLeft = Offset(strokePx / 2f, strokePx / 2f),
                    size = androidx.compose.ui.geometry.Size(size.toPx() - strokePx, size.toPx() - strokePx)
                )
            }
        }

        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}