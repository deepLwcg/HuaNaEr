package cn.yajienet.huanaer.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * 软糖质感统一圆角体系
 */
data class AppShapes(
    val cardSmall: RoundedCornerShape = RoundedCornerShape(16.dp),
    val cardMedium: RoundedCornerShape = RoundedCornerShape(20.dp),
    val cardLarge: RoundedCornerShape = RoundedCornerShape(24.dp),
    val cardExtraLarge: RoundedCornerShape = RoundedCornerShape(32.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(50),
    val button: RoundedCornerShape = RoundedCornerShape(24.dp),
    val numberPadKey: RoundedCornerShape = RoundedCornerShape(20.dp),
    val bottomSheet: RoundedCornerShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    val bottomBar: RoundedCornerShape = RoundedCornerShape(28.dp),
    val inputField: RoundedCornerShape = RoundedCornerShape(24.dp),
    val dialog: RoundedCornerShape = RoundedCornerShape(28.dp),
    val fab: RoundedCornerShape = RoundedCornerShape(50)
)

val LocalAppShapes = staticCompositionLocalOf { AppShapes() }
