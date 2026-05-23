package cn.yajienet.huanaer.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.staticCompositionLocalOf

val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)

data class AnimationSpecs(
    val pageTransitionDuration: Int = 300,
    val buttonPressScale: Float = 0.9f,
    val buttonPressDampingRatio: Float = 0.4f,
    val buttonPressStiffness: Float = 600f,
    val numberRollDuration: Int = 400,
    val numberRollDampingRatio: Float = 0.4f,
    val numberRollStiffness: Float = 500f,
    val progressDampingRatio: Float = 0.5f,
    val progressStiffness: Float = 300f,
    val listItemStaggerDelay: Int = 50,
    val listItemDampingRatio: Float = 0.5f,
    val listItemStiffness: Float = 500f,
    val celebrationDuration: Int = 1800,
    val celebrationParticleCount: Int = 60,
    val fabDampingRatio: Float = 0.45f,
    val fabStiffness: Float = 500f,
    val glowDriftDuration: Int = 6000
)

val LocalAnimationSpecs = staticCompositionLocalOf { AnimationSpecs() }

fun candyBounceSpring() = spring<Float>(
    dampingRatio = 0.5f,
    stiffness = 500f
)

fun candyPressSpring() = spring<Float>(
    dampingRatio = 0.4f,
    stiffness = 600f
)

fun candyListItemSpring() = spring<Float>(
    dampingRatio = 0.5f,
    stiffness = 500f
)

fun numberRollTween() = tween<Float>(
    durationMillis = 400,
    easing = EaseInOutCubic
)

fun progressSpring() = spring<Float>(
    dampingRatio = 0.5f,
    stiffness = 300f
)

fun pageTransitionTween() = tween<Int>(
    durationMillis = 300,
    easing = EaseOutCubic
)

fun deleteFlySpring() = spring<Float>(
    dampingRatio = 0.3f,
    stiffness = 400f
)
