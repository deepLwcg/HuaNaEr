package cn.yajienet.huanaer.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 集中定义动画规格
 * 柔光弥散风格使用舒缓的缓动函数
 */

// 缓动函数
val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
val EaseInCubic = CubicBezierEasing(0.55f, 0.055f, 0.675f, 0.19f)

// 动画规格定义
data class AnimationSpecs(
    // 页面过渡
    val pageTransitionDuration: Int = 300,

    // 按钮按压
    val buttonPressScale: Float = 0.96f,
    val buttonPressDampingRatio: Float = 0.6f,
    val buttonPressStiffness: Float = Spring.StiffnessMedium,

    // 数字滚动
    val numberRollDuration: Int = 500,

    // 进度指示器
    val progressDampingRatio: Float = 0.65f,
    val progressStiffness: Float = 200f,

    // 弥散光斑漂移
    val glowDriftDuration: Int = 8000,

    // 列表项入场
    val listItemFadeDuration: Int = 200,
    val listItemSlideOffset: Int = 20,
    val listItemStaggerDelay: Int = 60,

    // 庆祝动画
    val celebrationDuration: Int = 1500,
    val celebrationParticleCount: Int = 40
)

val LocalAnimationSpecs = staticCompositionLocalOf { AnimationSpecs() }

// 便捷工厂函数
fun buttonPressSpring() = spring<Float>(
    dampingRatio = 0.6f,
    stiffness = Spring.StiffnessMedium
)

fun numberRollTween() = tween<Float>(
    durationMillis = 500,
    easing = EaseInOutCubic
)

fun progressSpring() = spring<Float>(
    dampingRatio = 0.65f,
    stiffness = 200f
)

fun glowDriftTween() = tween<Float>(
    durationMillis = 8000,
    easing = LinearEasing
)

fun pageTransitionTween() = tween<Int>(
    durationMillis = 300,
    easing = EaseInOutCubic
)

fun listItemFadeTween() = tween<Float>(
    durationMillis = 200,
    easing = EaseOutCubic
)