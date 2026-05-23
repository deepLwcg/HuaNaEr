package cn.yajienet.huanaer.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * 统一圆角体系
 * 柔光弥散风格使用大圆角营造柔和感
 */
data class AppShapes(
    val cardSmall: RoundedCornerShape = RoundedCornerShape(12.dp),
    val cardMedium: RoundedCornerShape = RoundedCornerShape(16.dp),
    val cardLarge: RoundedCornerShape = RoundedCornerShape(20.dp),
    val cardExtraLarge: RoundedCornerShape = RoundedCornerShape(24.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(50),
    val button: RoundedCornerShape = RoundedCornerShape(12.dp),
    val numberPadKey: RoundedCornerShape = RoundedCornerShape(12.dp),
    val bottomSheet: RoundedCornerShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    val bottomBar: RoundedCornerShape = RoundedCornerShape(24.dp),
    val inputField: RoundedCornerShape = RoundedCornerShape(16.dp),
    val dialog: RoundedCornerShape = RoundedCornerShape(24.dp),
    val fab: RoundedCornerShape = RoundedCornerShape(20.dp)
)

val LocalAppShapes = staticCompositionLocalOf { AppShapes() }