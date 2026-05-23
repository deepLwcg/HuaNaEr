package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.progressSpring

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    gradient: Brush? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.25f),
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    centerContent: @Composable () -> Unit = {
        val animated by animateFloatAsState(progress, animationSpec = progressSpring(), label = "ring_pct")
        Text(
            text = "${(animated * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    },
    warning: Boolean = false
) {
    val ringColor = if (warning) MaterialTheme.colorScheme.error else color
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = progressSpring(),
        label = "ring_progress"
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val diameter = size.toPx() - strokePx
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)
            val arcSize = Size(diameter, diameter)
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = arcSize
            )

            val sweep = animatedProgress * 360f
            if (sweep > 0f) {
                drawArc(
                    brush = gradient ?: Brush.sweepGradient(listOf(ringColor, ringColor.copy(alpha = 0.6f))),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    topLeft = topLeft,
                    size = arcSize
                )
                val endRad = Math.toRadians((sweep - 90).toDouble())
                drawCircle(
                    color = ringColor.copy(alpha = 0.3f),
                    radius = strokePx,
                    center = Offset(
                        center.x + (diameter / 2f) * kotlin.math.cos(endRad).toFloat(),
                        center.y + (diameter / 2f) * kotlin.math.sin(endRad).toFloat()
                    )
                )
            }
        }
        centerContent()
    }
}
