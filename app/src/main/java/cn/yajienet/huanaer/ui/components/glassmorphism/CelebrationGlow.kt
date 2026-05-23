package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CelebrationGlow(
    trigger: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = extendedColorScheme()

    val particles = remember {
        List(40) {
            Particle(
                color = listOf(
                    extendedColors.glowPrimary,
                    extendedColors.glowSecondary,
                    extendedColors.glowAccent,
                    Color(0xFFFFD700),
                    Color(0xFFFF6B6B)
                ).random(),
                angle = Random.nextFloat() * 2f * PI.toFloat(),
                speed = Random.nextFloat() * 8f + 4f,
                size = Random.nextFloat() * 12f + 4f
            )
        }
    }

    val animatable = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(1500)
            )
            onFinished()
            animatable.snapTo(0f)
        }
    }

    val progress = animatable.value

    if (progress > 0f) {
        Box(modifier = modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val backgroundAlpha = (1f - progress) * 0.08f
                drawCircle(
                    color = extendedColors.glowPrimary.copy(alpha = backgroundAlpha),
                    radius = size.minDimension * 0.6f,
                    center = center
                )

                particles.forEach { particle ->
                    val distance = particle.speed * progress * 100f
                    val x = center.x + cos(particle.angle) * distance
                    val y = center.y + sin(particle.angle) * distance + progress * 50f

                    val alpha = (1f - progress) * 0.8f
                    val particleSize = particle.size * (1f - progress * 0.5f)

                    drawCircle(
                        color = particle.color.copy(alpha = alpha),
                        radius = particleSize,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

private data class Particle(
    val color: Color,
    val angle: Float,
    val speed: Float,
    val size: Float
)