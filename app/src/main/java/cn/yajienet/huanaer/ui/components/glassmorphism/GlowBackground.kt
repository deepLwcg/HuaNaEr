package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.AnimationSpecs
import cn.yajienet.huanaer.ui.theme.LocalAnimationSpecs
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import kotlin.math.sin

/**
 * 弥散光斑数据
 */
data class GlowSpot(
    val color: Color,
    val radius: Dp = 120.dp,
    val initialOffsetX: Dp = 0.dp,
    val initialOffsetY: Dp = 0.dp,
    val driftEnabled: Boolean = true,
    val driftAmplitudeX: Dp = 30.dp,
    val driftAmplitudeY: Dp = 20.dp
)

/**
 * 弥散光斑背景
 * 柔光弥散风格：绘制多个径向渐变光斑，带有缓慢漂移动画
 *
 * @param glowColors 光斑列表，默认取自 extendedColorScheme 的 glowPrimary/glowSecondary/glowAccent
 * @param content 前景内容
 */
@Composable
fun GlowBackground(
    modifier: Modifier = Modifier,
    glowColors: List<GlowSpot> = defaultGlowSpots(),
    content: @Composable () -> Unit
) {
    val animSpecs = LocalAnimationSpecs.current
    val infiniteTransition = rememberInfiniteTransition(label = "glow_drift")

    // 漂移相位动画（正弦曲线）
    val driftPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animSpecs.glowDriftDuration,
                easing = LinearEasing
            )
        ),
        label = "drift_phase"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // 光斑层
        Canvas(modifier = Modifier.fillMaxSize()) {
            glowColors.forEachIndexed { index, spot ->
                if (spot.color == Color.Transparent) return@forEachIndexed

                // 计算漂移偏移
                val offsetX = if (spot.driftEnabled) {
                    spot.initialOffsetX.toPx() + spot.driftAmplitudeX.toPx() * sin(driftPhase + index * 0.8f)
                } else {
                    spot.initialOffsetX.toPx()
                }
                val offsetY = if (spot.driftEnabled) {
                    spot.initialOffsetY.toPx() + spot.driftAmplitudeY.toPx() * sin(driftPhase + index * 1.2f)
                } else {
                    spot.initialOffsetY.toPx()
                }

                val center = Offset(offsetX + size.width / 2, offsetY + size.height / 2)
                val radiusPx = spot.radius.toPx()

                // 径向渐变：从中心 alpha 到边缘透明
                val gradient = Brush.radialGradient(
                    colors = listOf(
                        spot.color,
                        Color.Transparent
                    ),
                    center = center,
                    radius = radiusPx
                )

                drawCircle(
                    brush = gradient,
                    radius = radiusPx,
                    center = center
                )
            }
        }

        // 前景内容
        content()
    }
}

/**
 * 默认光斑配置
 * 取自 extendedColorScheme 的 glowPrimary/glowSecondary/glowAccent
 */
@Composable
fun defaultGlowSpots(): List<GlowSpot> {
    val extendedColors = extendedColorScheme()
    return remember(extendedColors) {
        listOf(
            GlowSpot(
                color = extendedColors.glowPrimary,
                radius = 150.dp,
                initialOffsetX = (-50).dp,
                initialOffsetY = (-80).dp,
                driftAmplitudeX = 40.dp,
                driftAmplitudeY = 30.dp
            ),
            GlowSpot(
                color = extendedColors.glowSecondary,
                radius = 120.dp,
                initialOffsetX = 80.dp,
                initialOffsetY = 50.dp,
                driftAmplitudeX = 30.dp,
                driftAmplitudeY = 25.dp
            ),
            GlowSpot(
                color = extendedColors.glowAccent,
                radius = 100.dp,
                initialOffsetX = (-30).dp,
                initialOffsetY = 100.dp,
                driftAmplitudeX = 20.dp,
                driftAmplitudeY = 15.dp,
                driftEnabled = true
            )
        )
    }
}