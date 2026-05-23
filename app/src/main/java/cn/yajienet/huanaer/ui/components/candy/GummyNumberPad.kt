package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.CandyTokens
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.ui.theme.candyPressSpring
import cn.yajienet.huanaer.ui.theme.extendedColorScheme

@Composable
fun GummyNumberPad(
    onDigit: (Int) -> Unit,
    onDecimal: () -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val extended = extendedColorScheme()
    val hapticClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GummyKey("1", onClick = { onDigit(1); hapticClick() })
            GummyKey("2", onClick = { onDigit(2); hapticClick() })
            GummyKey("3", onClick = { onDigit(3); hapticClick() })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GummyKey("4", onClick = { onDigit(4); hapticClick() })
            GummyKey("5", onClick = { onDigit(5); hapticClick() })
            GummyKey("6", onClick = { onDigit(6); hapticClick() })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GummyKey("7", onClick = { onDigit(7); hapticClick() })
            GummyKey("8", onClick = { onDigit(8); hapticClick() })
            GummyKey("9", onClick = { onDigit(9); hapticClick() })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GummyKey(".", onClick = { onDecimal(); hapticClick() })
            GummyKey("0", onClick = { onDigit(0); hapticClick() })
            GummyKey(
                text = "⌫",
                onClick = { onDelete(); hapticClick() },
                color = extended.expense.copy(alpha = 0.2f),
                textColor = extended.expense
            )
        }
        GummyKey(
            text = "完成",
            onClick = { onDone(); hapticClick() },
            isWide = true,
            color = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun GummyKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    isWide: Boolean = false
) {
    val shapes = LocalAppShapes.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = candyPressSpring(),
        label = "gummy_key"
    )
    val shadowTint = CandyTokens.coloredShadowColor(color)
    val keyMod = if (isWide) Modifier.fillMaxWidth().height(52.dp) else Modifier.size(68.dp)

    Box(
        modifier = modifier
            .then(keyMod)
            .scale(scale)
            .shadow(if (isPressed) 2.dp else 6.dp, shapes.numberPadKey, ambientColor = shadowTint, spotColor = shadowTint)
            .clip(shapes.numberPadKey)
            .background(color)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge, color = textColor)
    }
}
