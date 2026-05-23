package cn.yajienet.huanaer.ui.screens.budget

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.model.Budget
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
import cn.yajienet.huanaer.ui.components.glassmorphism.BudgetRing
import cn.yajienet.huanaer.ui.components.glassmorphism.NeumorphicCard
import cn.yajienet.huanaer.ui.components.glassmorphism.NeumorphicNumberPad
import cn.yajienet.huanaer.ui.components.glassmorphism.CategoryCircleButton
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onBudgetClick: (Long) -> Unit = {},
    addBudgetTrigger: Int = 0
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()

    var showAddBudgetSheet by remember { mutableStateOf(false) }

    // 监听 TopAppBar 添加按钮触发
    LaunchedEffect(addBudgetTrigger) {
        if (addBudgetTrigger > 0) {
            showAddBudgetSheet = true
        }
    }

    if (uiState.isLoading) {
        LoadingState()
    } else if (uiState.budgets.isEmpty()) {
        EmptyState(
            title = "暂无预算",
            subtitle = "点击右上角添加预算",
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 月度概览
            item {
                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "月度预算",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                CurrencyFormat.format(uiState.budgets.sumOf { it.amount }),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "已用",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val totalSpent = uiState.budgets.sumOf { it.spent }
                            val colors = extendedColorScheme()
                            Text(
                                CurrencyFormat.format(totalSpent),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (totalSpent > uiState.budgets.sumOf { it.amount })
                                    colors.budgetDanger else colors.income
                            )
                        }
                    }
                }
            }

            items(uiState.budgets, key = { it.id }) { budget ->
                BudgetCard(
                    budget = budget,
                    onClick = { onBudgetClick(budget.id) },
                    onDelete = { viewModel.deleteBudget(budget) }
                )
            }
        }
    }

    // 添加预算底部弹窗
    if (showAddBudgetSheet) {
        AddBudgetBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAddBudgetSheet = false }
        )
    }
}

@Composable
fun BudgetCard(
    budget: Budget,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = extendedColorScheme()
    val rawProgress = if (budget.amount > 0) (budget.spent / budget.amount).toFloat() else 0f
    val progress = rawProgress.coerceIn(0f, 1f)
    val isOverBudget = budget.spent > budget.amount
    val isWarning = rawProgress >= 0.8f && !isOverBudget

    // 接近限额脉冲动画（仅 warning/over 状态才创建）
    val shouldPulse = isWarning || isOverBudget
    val pulseAlpha = if (shouldPulse) {
        val infiniteTransition = rememberInfiniteTransition(label = "budgetPulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        )
        alpha
    } else 0f

    val borderColor = when {
        isOverBudget -> colors.budgetDanger.copy(alpha = 0.5f + 0.5f * pulseAlpha)
        isWarning -> colors.budgetWarning.copy(alpha = 0.5f + 0.5f * pulseAlpha)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    }

    val cardColor = if (isOverBudget) {
        colors.budgetDanger.copy(alpha = 0.05f + 0.05f * pulseAlpha)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    NeumorphicCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = cardColor,
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 环形进度
            BudgetRing(
                progress = progress,
                color = when {
                    isOverBudget -> colors.budgetDanger
                    isWarning -> colors.budgetWarning
                    else -> MaterialTheme.colorScheme.primary
                },
                size = 64.dp,
                strokeWidth = 6.dp,
                modifier = Modifier.size(64.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryCircleIcon(
                        name = budget.categoryName,
                        color = budget.categoryColor,
                        size = 24.dp,
                        textStyle = MaterialTheme.typography.labelSmall
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        budget.categoryName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${CurrencyFormat.format(budget.spent)} / ${CurrencyFormat.format(budget.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 删除按钮 - 柔光弥散风格小型圆形按钮
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "删除",
                    tint = colors.budgetDanger,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetBottomSheet(
    viewModel: BudgetViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategoryId by remember { mutableLongStateOf(uiState.categories.firstOrNull()?.id ?: -1L) }
    var amountText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "添加预算",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            // 分类选择
            Text("选择分类", style = MaterialTheme.typography.labelLarge)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.categories, key = { it.id }) { category ->
                    val selected = selectedCategoryId == category.id
                    CategoryCircleButton(
                        name = category.name,
                        color = try {
                            Color(category.color.removePrefix("#").toLong(16))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        },
                        selected = selected,
                        onClick = { selectedCategoryId = category.id },
                        size = 48.dp,
                        hapticEnabled = true
                    )
                }
            }

            // 金额显示
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("预算金额", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (amountText.isBlank()) "¥0.00" else "¥$amountText",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 数字键盘
            NeumorphicNumberPad(
                onDigit = { digit ->
                    val current = amountText
                    if (current.contains(".")) {
                        val afterDot = current.substringAfter(".")
                        if (afterDot.length >= 2) return@NeumorphicNumberPad
                    }
                    if (current == "0") {
                        amountText = digit.toString()
                        return@NeumorphicNumberPad
                    }
                    amountText = current + digit.toString()
                },
                onDecimal = {
                    if (amountText.isEmpty()) {
                        amountText = "0."
                    } else if (!amountText.contains(".")) {
                        amountText = "$amountText."
                    }
                },
                onDelete = {
                    if (amountText.isNotEmpty()) {
                        amountText = amountText.dropLast(1)
                    }
                },
                onDone = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && selectedCategoryId > 0) {
                        viewModel.addBudgetWithCategory(selectedCategoryId, amount)
                        onDismiss()
                    }
                }
            )
        }
    }
}