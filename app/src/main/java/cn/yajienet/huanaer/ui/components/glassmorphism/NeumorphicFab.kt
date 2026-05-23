package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import cn.yajienet.huanaer.ui.theme.LocalAppShapes

/**
 * 轻拟物风格 FAB
 */
@Composable
fun NeumorphicFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    iconContent: @Composable () -> Unit = {
        Icon(Icons.Filled.Add, contentDescription = "添加")
    },
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = contentColorFor(containerColor),
    size: Dp = 56.dp,
    elevation: Dp = 4.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shapes = LocalAppShapes.current

    val currentElevation = if (isPressed) elevation * 0.25f else elevation

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = shapes.fab,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = currentElevation,
            pressedElevation = currentElevation * 0.5f
        ),
        interactionSource = interactionSource,
        content = iconContent
    )
}