package cn.yajienet.huanaer.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cn.yajienet.huanaer.ui.components.glassmorphism.EmptyStateIllustration

/**
 * 空状态组件
 * @deprecated 使用 [EmptyStateIllustration] 替代，支持柔光弥散风格
 */
@Deprecated(
    message = "使用 EmptyStateIllustration 替代，支持柔光弥散风格",
    replaceWith = ReplaceWith(
        "EmptyStateIllustration(title, modifier, subtitle, icon)",
        "cn.yajienet.huanaer.ui.components.glassmorphism.EmptyStateIllustration"
    )
)
@Composable
fun EmptyState(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    // 桥接到 EmptyStateIllustration，将 ImageVector 转换为 Painter
    val painter: Painter? = icon?.let { rememberVectorPainter(it) }
    EmptyStateIllustration(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        icon = painter
    )
}