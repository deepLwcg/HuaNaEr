package cn.yajienet.huanaer.ui.screens.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.model.Budget
import cn.yajienet.huanaer.ui.theme.BudgetDanger
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BudgetListScreen(
    onAddBudgetClick: () -> Unit = {},
    onBudgetClick: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("预算管理") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Filled.Add, "添加预算")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "${uiState.currentYear}年${uiState.currentMonth}月预算",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )

            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else if (uiState.budgets.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("暂无预算设置")
                    Text(
                        "点击下方按钮添加预算",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.budgets) { budget ->
                        BudgetCard(
                            budget = budget,
                            currencyFormat = currencyFormat,
                            onDelete = { viewModel.deleteBudget(budget) }
                        )
                    }
                }
            }
        }
    }

    // Add Budget Dialog
    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            title = { Text("添加预算") },
            text = {
                Column {
                    Text("选择分类", style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        uiState.categories.forEach { category ->
                            FilterChip(
                                selected = uiState.selectedCategoryId == category.id,
                                onClick = { viewModel.setCategory(category.id) },
                                label = { Text(category.name) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uiState.budgetAmount,
                        onValueChange = { viewModel.setAmount(it) },
                        label = { Text("预算金额") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.addBudget() }) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideAddDialog() }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun BudgetCard(
    budget: Budget,
    currencyFormat: NumberFormat,
    onDelete: () -> Unit
) {
    val progress = (budget.spent / budget.amount).toFloat().coerceIn(0f, 1f)
    val isOverBudget = budget.spent > budget.amount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    budget.categoryName,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        "删除",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "已用: ${currencyFormat.format(budget.spent)}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = if (isOverBudget) BudgetDanger else androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "预算: ${currencyFormat.format(budget.amount)}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(8.dp)
            )

            if (isOverBudget) {
                Text(
                    "超出预算 ${currencyFormat.format(budget.spent - budget.amount)}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = BudgetDanger,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Text(
                    "剩余 ${currencyFormat.format(budget.amount - budget.spent)}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}