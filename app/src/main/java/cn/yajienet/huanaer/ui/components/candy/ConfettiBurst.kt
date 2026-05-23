package cn.yajienet.huanaer.ui.components.candy

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
import cn.yajienet.huanaer.ui.theme.CandyTokens
import cn.yajienet.huanaer.ui.theme.LocalAnimationSpecs
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ConfettiBurst(
    trigger: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val specs = LocalAnimationSpecs.current
    val palette = CandyTokens.candyPalette

    val particles = remember {
        List(specs.celebrationParticleCount) {
            ConfettiParticle(
                color = palette.random(),
                angle = Random.nextFloat() * 2f * PI.toFloat(),
                speed = Random.nextFloat() * 10f + 5f,
                size = Random.nextFloat() * 10f + 5f,
                isBean = Random.nextBoolean()
            )
        }
    }

    val animatable = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            animatable.animateTo(1f, animationSpec = tween(specs.celebrationDuration))
            onFinished()
            animatable.snapTo(0f)
        }
    }

    val progress = animatable.value
    if (progress > 0f) {
        Box(modifier = modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize()) {
                particles.forEach { p ->
                    val distance = p.speed * progress * 120f
                    val x = center.x + cos(p.angle) * distance
                    val y = center.y + sin(p.angle) * distance + progress * 80f
                    val alpha = (1f - progress).coerceIn(0f, 1f)
                    val r = p.size * (1f - progress * 0.4f)
                    if (p.isBean) {
                        drawRoundRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(x - r, y - r / 2),
                            size = androidx.compose.ui.geometry.Size(r * 2, r),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r / 2)
                        )
                    } else {
                        drawCircle(color = p.color.copy(alpha = alpha), radius = r, center = Offset(x, y))
                    }
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val color: Color,
    val angle: Float,
    val speed: Float,
    val size: Float,
    val isBean: Boolean
)
