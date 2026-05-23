package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.CandyTokens
import cn.yajienet.huanaer.ui.theme.LocalAnimationSpecs
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.ui.theme.candyPressSpring

@Composable
fun CandyFab(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    gradient: Brush = Brush.linearGradient(
        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
    ),
    expanded: Boolean = false,
    contentDescription: String? = null
) {
    val shapes = LocalAppShapes.current
    val specs = LocalAnimationSpecs.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) specs.buttonPressScale else 1f,
        animationSpec = candyPressSpring(),
        label = "fab_scale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = specs.fabDampingRatio,
            stiffness = specs.fabStiffness
        ),
        label = "fab_rotate"
    )

    val shadowColor = CandyTokens.coloredShadowColor(MaterialTheme.colorScheme.primary)

    Box(
        modifier = modifier
            .size(68.dp)
            .scale(scale)
            .shadow(12.dp, shapes.fab, ambientColor = shadowColor, spotColor = shadowColor)
            .clip(shapes.fab)
            .background(gradient)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(CandyTokens.cardHighlightOverlay())
        )
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.rotate(rotation)
        )
    }
}
