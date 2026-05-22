package cn.yajienet.huanaer.ui.screens.statistics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
import cn.yajienet.huanaer.ui.components.neubru.AnimatedCounter
import cn.yajienet.huanaer.ui.components.neubru.NeubruCard
import cn.yajienet.huanaer.ui.components.neubru.PillChip
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat

@Composable
fun StatisticsScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: StatisticsViewModel? = null
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val statsViewModel: StatisticsViewModel = viewModel ?: viewModel(
        factory = StatisticsViewModelFactory(application)
    )
    val uiState by statsViewModel.uiState.collectAsState()
    val colors = extendedColorScheme()

    if (uiState.isLoading) {
        LoadingState()
    } else if (uiState.expenseByCategory.isEmpty() && uiState.incomeByCategory.isEmpty()) {
        EmptyState(
            title = "本月暂无数据",
            subtitle = "开始记账后可查看统计",
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
            // 时间范围 PillChip
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val ranges = listOf(
                        TimeRange.THIS_MONTH to "本月",
                        TimeRange.LAST_3_MONTHS to "近3月",
                        TimeRange.LAST_6_MONTHS to "近6月",
                        TimeRange.THIS_YEAR to "今年"
                    )
                    items(ranges, key = { it.first }) { (range, label) ->
                        PillChip(
                            text = {
                                Text(
                                    label,
                                    color = if (uiState.selectedTimeRange == range)
                                        MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            selected = uiState.selectedTimeRange == range,
                            onClick = { statsViewModel.setTimeRange(range) },
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 收入支出汇总 — NeubruCard
            item {
                NeubruCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "收入",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AnimatedCounter(
                                targetValue = uiState.totalIncome,
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.income
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "支出",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AnimatedCounter(
                                targetValue = uiState.totalExpense,
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.expense
                            )
                        }
                    }
                }
            }

            // 环形图
            if (uiState.expenseByCategory.isNotEmpty()) {
                item {
                    NeubruCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "支出分布",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(12.dp))
                            DonutChart(
                                data = uiState.expenseByCategory,
                                totalAmount = uiState.totalExpense,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                    }
                }

                // 支出详情
                item {
                    Text(
                        "支出详情",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.expenseByCategory, key = { it.categoryId }) { stat ->
                    NeubruCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 12.dp
                    ) {
                        CategoryStatRow(
                            stat = stat,
                            total = uiState.totalExpense,
                            isExpense = true,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // 收入详情
            if (uiState.incomeByCategory.isNotEmpty()) {
                item {
                    Text(
                        "收入详情",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.incomeByCategory, key = { it.categoryId }) { stat ->
                    NeubruCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 12.dp
                    ) {
                        CategoryStatRow(
                            stat = stat,
                            total = uiState.totalIncome,
                            isExpense = false,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    data: List<CategoryStatistics>,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val animationProgress = remember { Animatable(0f) }
    val primaryFallback = MaterialTheme.colorScheme.primary
    val pieColors = remember(data) {
        data.map {
            try { Color(android.graphics.Color.parseColor(it.categoryColor)) }
            catch (_: IllegalArgumentException) { primaryFallback }
        }
    }
    val totalPercentage = remember(data) { data.fold(0f) { acc, stat -> acc + stat.percentage } }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
    }

    val strokeWidth = 24.dp
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = modifier) {
            val strokePx = strokeWidth.toPx()
            val diameter = minOf(size.width, size.height) - strokePx
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // 背景环
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokePx)
            )

            var startAngle = -90f
            val gapAngle = if (data.size > 1) 2f else 0f
            data.forEachIndexed { index, stat ->
                val sweepAngle = (stat.percentage / totalPercentage) * (360f - gapAngle * data.size) * animationProgress.value
                val color = pieColors[index]

                drawArc(
                    color = color,
                    startAngle = startAngle + gapAngle / 2,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2,radius * 2),
                    style = Stroke(
                        width = strokePx,
                        cap = StrokeCap.Butt
                    )
                )
                startAngle += sweepAngle + gapAngle
            }
        }

        // 中心总额
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = CurrencyFormat.format(totalAmount),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = onSurfaceColor
            )
            Text(
                "总支出",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryStatRow(
    stat: CategoryStatistics,
    total: Double,
    isExpense: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = extendedColorScheme()
    val progressColor = if (isExpense) colors.expense else colors.income
    val parsedColor = remember(stat.categoryColor) {
        Color(android.graphics.Color.parseColor(stat.categoryColor))
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryCircleIcon(
            name = stat.categoryName,
            color = stat.categoryColor,
            size = 32.dp,
            textStyle = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stat.categoryName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            // 粗边框进度条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(stat.percentage.coerceIn(0f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(progressColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        AnimatedCounter(
            targetValue = stat.amount,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = progressColor
        )
    }
}