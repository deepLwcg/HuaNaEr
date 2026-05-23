package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.CandyTokens
import cn.yajienet.huanaer.ui.theme.LocalAnimationSpecs
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.ui.theme.candyPressSpring

@Composable
fun GummyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: Brush? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    height: Dp = 56.dp
) {
    val shapes = LocalAppShapes.current
    val specs = LocalAnimationSpecs.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) specs.buttonPressScale else 1f,
        animationSpec = candyPressSpring(),
        label = "gummy_btn_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .clip(shapes.button)
            .then(
                if (gradient != null) Modifier.background(gradient)
                else Modifier.background(if (enabled) containerColor else containerColor.copy(alpha = 0.5f))
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(CandyTokens.cardHighlightOverlay())
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
