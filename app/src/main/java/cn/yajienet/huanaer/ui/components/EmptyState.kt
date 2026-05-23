package cn.yajienet.huanaer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.yajienet.huanaer.ui.components.candy.EmptyStateSticker

@Composable
fun EmptyState(
    title: String,
    subtitle: String? = null,
    emoji: String = "💰",
    modifier: Modifier = Modifier
) {
    val message = if (subtitle != null) "$title\n$subtitle" else title
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        EmptyStateSticker(emoji = emoji, message = message)
    }
}
