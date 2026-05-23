package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.CandyTokens
import cn.yajienet.huanaer.ui.theme.LocalAppShapes

@Composable
fun CandyCard(
    modifier: Modifier = Modifier,
    containerColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    gradient: Brush? = null,
    shadowColor: Color? = null,
    shape: Shape = LocalAppShapes.current.cardLarge,
    elevation: Dp = 8.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val tint = shadowColor ?: CandyTokens.coloredShadowColor(containerColor)

    Box(
        modifier = modifier
            .shadow(elevation, shape, ambientColor = tint, spotColor = tint)
            .clip(shape)
            .then(
                if (gradient != null) Modifier.background(gradient)
                else Modifier.background(containerColor)
            )
            .padding(contentPadding),
        content = content
    )
}
