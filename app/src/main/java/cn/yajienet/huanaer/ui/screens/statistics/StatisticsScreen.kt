package cn.yajienet.huanaer.ui.screens.statistics

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.theme.ExpenseRed
import cn.yajienet.huanaer.ui.theme.IncomeGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("统计") })
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Month selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(Icons.Filled.ChevronLeft, "上个月")
                    }
                    Text(
                        text = "${uiState.selectedYear}年${uiState.selectedMonth}月",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.Filled.ChevronRight, "下个月")
                    }
                }

                // Summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("收入", style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                                Text(
                                    currencyFormat.format(uiState.totalIncome),
                                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                    color = IncomeGreen
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("支出", style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                                Text(
                                    currencyFormat.format(uiState.totalExpense),
                                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                    color = ExpenseRed
                                )
                            }
                        }
                    }
                }

                // Expense by category
                if (uiState.expenseByCategory.isNotEmpty()) {
                    Text(
                        text = "支出分布",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Simple pie chart visualization
                            SimplePieChart(
                                data = uiState.expenseByCategory,
                                modifier = Modifier
                                    .size(120.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn {
                                items(uiState.expenseByCategory) { stat ->
                                    CategoryStatItem(stat, uiState.totalExpense)
                                }
                            }
                        }
                    }
                }

                // Income by category
                if (uiState.incomeByCategory.isNotEmpty()) {
                    Text(
                        text = "收入分布",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            LazyColumn {
                                items(uiState.incomeByCategory) { stat ->
                                    CategoryStatItem(stat, uiState.totalIncome, isIncome = true)
                                }
                            }
                        }
                    }
                }

                // Empty state
                if (uiState.expenseByCategory.isEmpty() && uiState.incomeByCategory.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("本月暂无数据")
                    }
                }
            }
        }
    }
}

@Composable
fun SimplePieChart(
    data: List<CategoryStatistics>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val totalPercentage = data.sumOf { it.percentage.toDouble() }
    var currentAngle = 0f

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Gray)
    ) {
        // Simple representation - colored segments would need canvas drawing
        // For simplicity, just show colored dots representing each category
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            data.take(5).forEach { stat ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(android.graphics.Color.parseColor(stat.categoryColor)), CircleShape)
                )
            }
        }
    }
}

@Composable
fun CategoryStatItem(
    stat: CategoryStatistics,
    total: Double,
    isIncome: Boolean = false
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color(android.graphics.Color.parseColor(stat.categoryColor)), CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(stat.categoryName, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { stat.percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(top = 4.dp),
                color = if (isIncome) IncomeGreen else ExpenseRed
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            currencyFormat.format(stat.amount),
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = if (isIncome) IncomeGreen else ExpenseRed
        )
    }
}