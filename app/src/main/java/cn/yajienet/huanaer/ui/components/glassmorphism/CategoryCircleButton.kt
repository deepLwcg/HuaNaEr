package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon

/**
 * 圆形分类选择按钮
 * 柔光弥散风格：选中极细描边 + 微弱弥散光
 *
 * @param category 分类名称
 * @param color 分类颜色
 * @param selected 是否选中
 * @param onClick 点击回调
 * @param size 按钮尺寸
 * @param hapticEnabled 是否启用触觉反馈
 */
@Composable
fun CategoryCircleButton(
    name: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    hapticEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "category_button_scale"
    )

    val borderColor = if (selected) color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val borderWidth = if (selected) 1.5.dp else 0.5.dp

    // 微弱弥散光背景（选中时）
    val glowColor = if (selected) color.copy(alpha = 0.1f) else Color.Transparent

    Box(
        modifier = modifier
            .size(size + 8.dp)  // 外圈包含弥散光
            .background(
                color = glowColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(size)
                .scale(scale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        if (hapticEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        onClick()
                    }
                ),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
            border = BorderStroke(borderWidth, borderColor),
            content = {
                Box(
                    modifier = Modifier.size(size),
                    contentAlignment = Alignment.Center
                ) {
                    CategoryCircleIcon(
                        name = name,
                        color = "#${color.value.toString().substring(3)}",  // Color 转十六进制字符串
                        size = size - 8.dp
                    )
                }
            }
        )
    }
}