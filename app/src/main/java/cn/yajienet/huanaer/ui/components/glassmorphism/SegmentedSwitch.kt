package cn.yajienet.huanaer.ui.components.glassmorphism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.LocalAppShapes

/**
 * 双段切换器
 * 用于收入/支出类型切换场景
 *
 * @param selected 选中项索引（0=左侧，1=右侧）
 * @param onSelectedChange 选中变化回调
 * @param leftText 左侧文本（如"支出"）
 * @param rightText 右侧文本（如"收入"）
 * @param selectedColor 选中时的主色
 */
@Composable
fun SegmentedSwitch(
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    leftText: String,
    rightText: String,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primary
) {
    val shapes = LocalAppShapes.current

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {
        SegmentedButton(
            selected = selected == 0,
            onClick = { onSelectedChange(0) },
            shape = SegmentedButtonDefaults.itemShape(
                index = 0,
                count = 2,
                baseShape = shapes.pill
            ),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = selectedColor.copy(alpha = 0.2f),
                activeContentColor = selectedColor,
                activeBorderColor = selectedColor,
                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            androidx.compose.material3.Text(leftText)
        }

        SegmentedButton(
            selected = selected == 1,
            onClick = { onSelectedChange(1) },
            shape = SegmentedButtonDefaults.itemShape(
                index = 1,
                count = 2,
                baseShape = shapes.pill
            ),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = selectedColor.copy(alpha = 0.2f),
                activeContentColor = selectedColor,
                activeBorderColor = selectedColor,
                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            androidx.compose.material3.Text(rightText)
        }
    }
}