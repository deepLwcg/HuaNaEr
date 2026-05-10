package cn.yajienet.huanaer.ui.screens.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
import cn.yajienet.huanaer.ui.components.MonthYearSelector
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()
    val colors = extendedColorScheme()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("统计分析") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingState(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Month selector
                MonthYearSelector(
                    year = uiState.selectedYear,
                    month = uiState.selectedMonth,
                    onPrevious = { viewModel.previousMonth() },
                    onNext = { viewModel.nextMonth() },
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                AnimatedVisibility(
                    visible = !uiState.isLoading,
                    enter = fadeIn()
                ) {
                    Column {
                        // Summary card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Income
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

                                // Divider
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                )

                                // Expense
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

                        Spacer(modifier = Modifier.height(24.dp))

                        // Expense by category
                        if (uiState.expenseByCategory.isNotEmpty()) {
                            Text(
                                text = "支出分布",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Pie chart
                                    PieChart(
                                        data = uiState.expenseByCategory,
                                        modifier = Modifier
                                            .size(160.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Category list
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(uiState.expenseByCategory) { stat ->
                                            CategoryStatRow(
                                                stat = stat,
                                                total = uiState.totalExpense,
                                                isExpense = true
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Income by category
                        if (uiState.incomeByCategory.isNotEmpty()) {
                            Text(
                                text = "收入分布",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(uiState.incomeByCategory) { stat ->
                                            CategoryStatRow(
                                                stat = stat,
                                                total = uiState.totalIncome,
                                                isExpense = false
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Empty state
                        if (uiState.expenseByCategory.isEmpty() && uiState.incomeByCategory.isEmpty()) {
                            EmptyState(
                                title = "本月暂无数据",
                                subtitle = "开始记账后可查看统计",
                                modifier = Modifier.fillMaxSize().weight(1f)
                            )
                        }
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

    Canvas(modifier = modifier) {
        var startAngle = 0f

        data.forEach { stat ->
            val sweepAngle = stat.percentage * 360f
            val color = Color(android.graphics.Color.parseColor(stat.categoryColor))

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                style = Stroke(width = 32.dp.toPx()),
                size = size
            )

            startAngle += sweepAngle
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

        Spacer(modifier = Modifier.width(12.dp))

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

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = CurrencyFormat.format(stat.amount),
            style = MaterialTheme.typography.bodyMedium,
            color = progressColor
        )
    }
}