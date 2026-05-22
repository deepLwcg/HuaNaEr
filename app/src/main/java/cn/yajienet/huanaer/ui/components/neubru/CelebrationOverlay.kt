package cn.yajienet.huanaer.ui.components.neubru

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun CelebrationOverlay(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    if (!trigger) return

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        Color(0xFFFFD700),
        Color(0xFFFF6B6B)
    )

    val particles = remember {
        List(40) {
            val angle = Random.nextFloat() * 360f
            val speed = Random.nextFloat() * 8f + 2f
            val radians = Math.toRadians(angle.toDouble())
            Particle(
                x = 0.5f,
                y = 0.5f,
                vx = cos(radians).toFloat() * speed,
                vy = sin(radians).toFloat() * speed,
                color = colors[Random.nextInt(colors.size)],
                size = Random.nextFloat() * 12f + 4f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 10f - 5f
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        particles.forEach { particle ->
            val gravity = 0.15f
            val currentVy = particle.vy + gravity * p * 10f
            val x = particle.x + particle.vx * p
            val y = particle.y + currentVy * p
            val alpha = (1f - p).coerceIn(0f, 1f)
            val particleSize = particle.size * (1f - p * 0.5f)

            drawRect(
                color = particle.color.copy(alpha = alpha),
                topLeft = Offset(
                    x = x * size.width - particleSize / 2f,
                    y = y * size.height - particleSize / 2f
                ),
                size = Size(particleSize, particleSize),
                alpha = alpha
            )
        }
    }
}