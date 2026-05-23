package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.ui.theme.extendedColorScheme

@Composable
fun NeumorphicNumberPad(
    onDigit: (Int) -> Unit,
    onDecimal: () -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val extendedColors = extendedColorScheme()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberPadKey("1", onClick = { onDigit(1); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
            NumberPadKey("2", onClick = { onDigit(2); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
            NumberPadKey("3", onClick = { onDigit(3); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberPadKey("4", onClick = { onDigit(4); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
            NumberPadKey("5", onClick = { onDigit(5); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
            NumberPadKey("6", onClick = { onDigit(6); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberPadKey("7", onClick = { onDigit(7); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
            NumberPadKey("8", onClick = { onDigit(8); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
            NumberPadKey("9", onClick = { onDigit(9); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberPadKey(".", onClick = { onDecimal(); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
            NumberPadKey("0", onClick = { onDigit(0); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) })
            NumberPadKey("⌫", onClick = { onDelete(); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }, color = extendedColors.expense)
        }
        NumberPadKey(
            "完成",
            onClick = { onDone(); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
            isWide = true,
            color = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun NumberPadKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    isWide: Boolean = false
) {
    val shapes = LocalAppShapes.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "key_scale"
    )

    val keyModifier = if (isWide) {
        Modifier.fillMaxWidth().height(56.dp)
    } else {
        Modifier.size(72.dp)
    }

    Surface(
        modifier = modifier
            .then(keyModifier)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = shapes.numberPadKey,
        color = color,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        tonalElevation = if (isPressed) 0.dp else 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = textColor
            )
        }
    }
}