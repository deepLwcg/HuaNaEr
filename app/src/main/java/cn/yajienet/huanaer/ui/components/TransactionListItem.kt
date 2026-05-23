package cn.yajienet.huanaer.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.data.model.Transaction
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.components.candy.CandyCard
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.ui.theme.deleteFlySpring
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CategoryEmoji
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

    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var contentHeight by remember { mutableStateOf(0) }
    var isSwipeRevealing by remember { mutableStateOf(false) }

    val deleteButtonWidth = 80.dp
    val deleteButtonWidthPx = with(LocalDensity.current) { deleteButtonWidth.toPx() }
    val maxDragDistance = deleteButtonWidthPx * 1.5f

    val revealDelete = isExpanded
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            isSwipeRevealing = false
        }
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
            .clip(shapes.cardMedium)
    ) {
        // 删除层：与卡片同圆角裁剪，滑出前透明避免灰底
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    with(LocalDensity.current) {
                        if (contentHeight > 0) Modifier.height(contentHeight.toDp()) else Modifier
                    }
                )
                .clip(shapes.cardMedium)
                .background(
                    if (revealDelete || isSwipeRevealing) colors.expenseContainer else Color.Transparent
                ),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (revealDelete) {
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "删除",
                        tint = colors.expense,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // 不透明前景卡片（裁剪圆角 + 无阴影，避免左滑露出直角灰影）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size -> contentHeight = size.height }
                .clip(shapes.cardMedium)
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val targetValue = if (offsetX.value < -deleteButtonWidthPx / 2) {
                                    -deleteButtonWidthPx
                                } else {
                                    0f
                                }
                                isSwipeRevealing = targetValue < 0f
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
                                if (offsetX.value == 0f && dragAmount < 0) {
                                    onExpand()
                                }
                                val newOffset = offsetX.value + dragAmount
                                offsetX.snapTo(newOffset.coerceIn(-maxDragDistance, 0f))
                                isSwipeRevealing = offsetX.value < -deleteButtonWidthPx * 0.05f
                            }
                        }
                    )
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = offsetX.value == 0f,
                    onClick = onClick
                )
        ) {
            CandyCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                shape = shapes.cardMedium,
                elevation = 0.dp,
                contentPadding = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryEmojiIcon(
                        emoji = CategoryEmoji.resolve(transaction.categoryIcon, transaction.categoryName),
                        colorHex = transaction.categoryColor
                    )

                    Spacer(modifier = Modifier.width(12.dp))

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
                        text = CurrencyFormat.formatSigned(
                            transaction.amount,
                            transaction.type == TransactionType.INCOME
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = amountColor
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条交易记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        scope.launch {
                            offsetX.animateTo(-maxDragDistance * 2, deleteFlySpring())
                            onDelete()
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
