package cn.yajienet.huanaer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import cn.yajienet.huanaer.data.datastore.ThemeStyle

object CandyTokens {

    fun primaryColor(style: ThemeStyle): Color = when (style) {
        ThemeStyle.STRAWBERRY_SHAKE -> CandyPink
        ThemeStyle.SEA_SALT_SODA -> CandyBlue
        ThemeStyle.GRAPE_BUBBLE -> CandyPurple
    }

    fun secondaryColor(style: ThemeStyle): Color = when (style) {
        ThemeStyle.STRAWBERRY_SHAKE -> CandyYellow
        ThemeStyle.SEA_SALT_SODA -> CandyYellow
        ThemeStyle.GRAPE_BUBBLE -> CandyOrange
    }

    fun primaryGradient(style: ThemeStyle): Brush = Brush.linearGradient(
        colors = listOf(primaryColor(style), secondaryColor(style)),
        start = Offset.Zero,
        end = Offset(400f, 400f)
    )

    fun cardHighlightOverlay(): Brush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.35f),
            Color.White.copy(alpha = 0.08f),
            Color.Transparent
        ),
        startY = 0f,
        endY = 120f
    )

    /** 彩色果冻阴影：取主色加深约 30% */
    fun coloredShadowColor(base: Color): Color {
        return Color(
            red = (base.red * 0.7f).coerceIn(0f, 1f),
            green = (base.green * 0.7f).coerceIn(0f, 1f),
            blue = (base.blue * 0.7f).coerceIn(0f, 1f),
            alpha = 0.35f
        )
    }

    val candyPalette: List<Color> = listOf(
        CandyPink, CandyBlue, CandyYellow, CandyPurple, CandyOrange,
        CandyIncome, Color(0xFFFFB6C1), Color(0xFF98D8C8)
    )
}

@Composable
fun rememberCandyPrimaryGradient(style: ThemeStyle): Brush = CandyTokens.primaryGradient(style)
