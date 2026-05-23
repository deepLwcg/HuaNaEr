package cn.yajienet.huanaer.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.data.model.Transaction
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat
import cn.yajienet.huanaer.util.DateUtils
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit,
    onDelete: () -> Unit = {},
    isExpanded: Boolean = false,
    onExpand: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val shapes = LocalAppShapes.current
    val colors = extendedColorScheme()
    val amountColor = when (transaction.type) {
        TransactionType.INCOME -> colors.income
        TransactionType.EXPENSE -> colors.expense
    }
    val amountPrefix = if (transaction.type == TransactionType.INCOME) "+" else "-"

    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var contentHeight by remember { mutableStateOf(0) }

    val deleteButtonWidth = 80.dp
    val deleteButtonWidthPx = with(LocalDensity.current) { deleteButtonWidth.toPx() }
    // 可以滑动的最大距离，超过删除按钮宽度，有弹性效果
    val maxDragDistance = deleteButtonWidthPx * 1.5f

    // 当不是展开项时，动画收回
    LaunchedEffect(isExpanded) {
        if (!isExpanded && offsetX.value < 0f) {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 删除按钮背景层 - 柔和 expense 色背景
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { contentHeight.toDp() })
                .background(
                    color = colors.expense.copy(alpha = 0.1f),
                    shape = shapes.cardMedium
                )
                .clip(shapes.cardMedium),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "删除",
                    tint = colors.expense,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 可滑动的内容层
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size -> contentHeight = size.height }
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                // 滑动结束后，动画到目标位置
                                val targetValue = if (offsetX.value < -deleteButtonWidthPx / 2) {
                                    // 弹回到固定显示删除按钮的位置
                                    -deleteButtonWidthPx
                                } else {
                                    0f
                                }
                                if (targetValue < 0f) {
                                    onExpand()
                                }
                                offsetX.animateTo(
                                    targetValue = targetValue,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                // 开始向左拖动时，立即通知父组件收回其他项
                                if (offsetX.value == 0f && dragAmount < 0) {
                                    onExpand()
                                }
                                val newOffset = offsetX.value + dragAmount
                                // 拖动时可以超过删除按钮宽度，有弹性效果
                                offsetX.snapTo(newOffset.coerceIn(-maxDragDistance, 0f))
                            }
                        }
                    )
                }
                .clickable(enabled = offsetX.value == 0f) { onClick() },
            shape = shapes.cardMedium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryCircleIcon(
                    name = transaction.categoryName,
                    color = transaction.categoryColor,
                    size = 40.dp,
                    textStyle = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.categoryName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = transaction.note ?: DateUtils.formatDate(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = "$amountPrefix${CurrencyFormat.format(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = amountColor
                )
            }
        }
    }

    // 删除确认对话框
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条交易记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                        scope.launch {
                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        scope.launch {
                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}