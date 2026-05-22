package cn.yajienet.huanaer.ui.screens.budget

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.model.Budget
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
import cn.yajienet.huanaer.ui.components.neubru.BouncyIconButton
import cn.yajienet.huanaer.ui.components.neubru.NeubruCard
import cn.yajienet.huanaer.ui.components.neubru.NeubruNumberPad
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
                NeubruCard(modifier = Modifier.fillMaxWidth()) {
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

    NeubruCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = borderColor,
        backgroundColor = cardColor,
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 环形进度
            RingProgressIndicator(
                progress = progress,
                color = when {
                    isOverBudget -> colors.budgetDanger
                    isWarning -> colors.budgetWarning
                    else -> MaterialTheme.colorScheme.primary
                },
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

            BouncyIconButton(
                onClick = onDelete,
                size = 36.dp
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

@Composable
fun RingProgressIndicator(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "ringProgress"
    )
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = modifier) {
            val stroke = 6.dp.toPx()
            val diameter = minOf(size.width, size.height) - stroke
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // 背景环
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = stroke)
            )

            // 进度环
            val sweepAngle = animatedProgress * 360f
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        // 中心百分比
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = color
        )
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
                    BouncyIconButton(
                        onClick = { selectedCategoryId = category.id },
                        size = 56.dp,
                        hapticEnabled = true
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CategoryCircleIcon(
                                name = category.name,
                                color = category.color,
                                size = if (selected) 52.dp else 44.dp
                            )
                            if (selected) {
                                androidx.compose.material3.Surface(
                                    modifier = Modifier.size(56.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
                                ) {}
                            }
                        }
                    }
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
            NeubruNumberPad(
                onDigit = { digit ->
                    val current = amountText
                    if (current.contains(".")) {
                        val afterDot = current.substringAfter(".")
                        if (afterDot.length >= 2) return@NeubruNumberPad
                    }
                    if (current == "0" && digit != ".") {
                        amountText = digit
                        return@NeubruNumberPad
                    }
                    amountText = current + digit
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