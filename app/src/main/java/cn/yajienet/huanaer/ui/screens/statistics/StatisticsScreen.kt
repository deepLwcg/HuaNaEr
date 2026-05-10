package cn.yajienet.huanaer.ui.screens.statistics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
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
            // 收入支出汇总卡片
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 收入 - 占50%宽度
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "收入",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormat.format(uiState.totalIncome),
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.income,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // 分隔线
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        // 支出 - 占50%宽度
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "支出",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormat.format(uiState.totalExpense),
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.expense,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // 支出分布饼图
            if (uiState.expenseByCategory.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            PieChart(
                                data = uiState.expenseByCategory,
                                modifier = Modifier.size(220.dp)
                            )
                        }
                    }
                }

                // 支出详情列表标题
                item {
                    Text(
                        text = "支出详情",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // 支出详情列表项
                items(uiState.expenseByCategory) { stat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
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

            // 收入分布
            if (uiState.incomeByCategory.isNotEmpty()) {
                // 收入详情列表标题
                item {
                    Text(
                        text = "收入详情",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // 收入详情列表项
                items(uiState.incomeByCategory) { stat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
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
fun PieChart(
    data: List<CategoryStatistics>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val animationProgress = remember { Animatable(0f) }

    // 使用主题颜色，支持深色/浅色模式切换
    val textColor = MaterialTheme.colorScheme.onSurface
    val lineColor = MaterialTheme.colorScheme.outlineVariant
    val surfaceColor = MaterialTheme.colorScheme.surface

    // 预计算总百分比和颜色，避免在 Canvas 中重复计算
    val totalPercentage = remember(data) { data.fold(0f) { acc, stat -> acc + stat.percentage } }
    val pieColors = remember(data) {
        data.map { stat -> Color(android.graphics.Color.parseColor(stat.categoryColor)) }
    }

    // 只在数据变化时触发动画，避免每次重组都重新动画
    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        )
    }

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = minOf(centerX, centerY) * 0.7f
        val innerRadius = radius * 0.35f
        val outerLabelRadius = radius + 40f

        var startAngle = -90f

        data.forEachIndexed { index, stat ->
            val sweepAngle = (stat.percentage / totalPercentage) * 360f * animationProgress.value
            val color = pieColors[index]

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                style = Fill,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2)
            )

            drawArc(
                color = surfaceColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                style = Stroke(width = 2.dp.toPx()),
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2)
            )

            // Draw label outside with connecting line
            if (animationProgress.value > 0.95f && sweepAngle > 15f) {
                val midAngle = startAngle + sweepAngle / 2f
                val radians = (midAngle * kotlin.math.PI / 180f).toFloat()

                // Point on the pie edge
                val pieEdgeX = centerX + radius * kotlin.math.cos(radians)
                val pieEdgeY = centerY + radius * kotlin.math.sin(radians)

                // Point outside for label
                val labelX = centerX + outerLabelRadius * kotlin.math.cos(radians)
                val labelY = centerY + outerLabelRadius * kotlin.math.sin(radians)

                // Draw connecting line and text using native canvas
                val nativeCanvas = drawContext.canvas.nativeCanvas

                // Line paint
                val linePaint = android.graphics.Paint().apply {
                    this.color = lineColor.toArgb()
                    strokeWidth = 2f
                    isAntiAlias = true
                }
                nativeCanvas.drawLine(pieEdgeX, pieEdgeY, labelX, labelY, linePaint)
                nativeCanvas.drawCircle(pieEdgeX, pieEdgeY, 6f, linePaint)

                // Text paint
                val textPaint = android.graphics.Paint().apply {
                    this.color = textColor.toArgb()
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.LEFT
                    isAntiAlias = true
                    setTypeface(android.graphics.Typeface.DEFAULT_BOLD)
                }

                // Draw label text
                val percentageText = "${(stat.percentage * 100).toInt()}%"
                val labelText = "${stat.categoryName} $percentageText"

                // Adjust text position based on angle (left or right side)
                val textX = if (kotlin.math.cos(radians) >= 0) labelX + 8f else labelX - textPaint.measureText(labelText) - 8f
                val textY = labelY + textPaint.textSize / 3f

                nativeCanvas.drawText(labelText, textX, textY, textPaint)
            }

            startAngle += sweepAngle
        }

        drawCircle(
            color = surfaceColor,
            radius = innerRadius,
            center = Offset(centerX, centerY)
        )
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

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryCircleIcon(
            name = stat.categoryName,
            color = stat.categoryColor,
            size = 28.dp,
            textStyle = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stat.categoryName,
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = { stat.percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = CurrencyFormat.format(stat.amount),
            style = MaterialTheme.typography.bodyMedium,
            color = progressColor
        )
    }
}