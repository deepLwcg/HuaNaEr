package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.yajienet.huanaer.ui.theme.candyBounceSpring

@Composable
fun EmojiCategoryChip(
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    glowColor: Color = MaterialTheme.colorScheme.primary
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 0.9f,
        animationSpec = candyBounceSpring(),
        label = "emoji_chip_scale"
    )

    Box(
        modifier = modifier
            .size(if (selected) 64.dp else 52.dp)
            .scale(scale)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(backgroundColor.copy(alpha = if (selected) 1f else 0.6f))
            .then(
                if (selected) Modifier.border(3.dp, glowColor.copy(alpha = 0.8f), MaterialTheme.shapes.extraLarge)
                else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = if (selected) 28.sp else 22.sp)
    }
}
