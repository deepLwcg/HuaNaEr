package cn.yajienet.huanaer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 字形外框下留白偏多，需上移（负值 = 向上） */
private val emojiOpticalYOffset: Map<String, Dp> = mapOf(
    "🚗" to (-4).dp,
    "🚙" to (-4).dp,
    "🚕" to (-4).dp,
    "🚌" to (-4).dp,
)

/**
 * 分类 emoji 圆形底图，去掉字体内边距以保证在圆内视觉居中
 */
@Composable
fun CategoryEmojiIcon(
    emoji: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    emojiSize: TextUnit = 24.sp
) {
    val fallback = MaterialTheme.colorScheme.primaryContainer
    val backgroundColor = remember(colorHex, fallback) {
        try {
            Color(colorHex.removePrefix("#").toLong(16) or 0xFF000000)
        } catch (_: Exception) {
            fallback
        }.copy(alpha = 0.35f)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = TextStyle(
                fontSize = emojiSize,
                lineHeight = emojiSize,
                textAlign = TextAlign.Center,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            ),
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .offset(y = emojiOpticalYOffset[emoji] ?: 0.dp)
        )
    }
}
