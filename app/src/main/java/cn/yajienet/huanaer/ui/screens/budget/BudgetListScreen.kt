package cn.yajienet.huanaer.ui.screens.budget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat
import cn.yajienet.huanaer.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    val colors = extendedColorScheme()

    // Handle add budget trigger from FAB - trigger count is tracked in ViewModel
    LaunchedEffect(addBudgetTrigger) {
        viewModel.handleAddTrigger(addBudgetTrigger)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
    ) {
        Text(
            text = DateUtils.formatMonthYear(uiState.currentMonth, uiState.currentYear),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            LoadingState()
        } else if (uiState.budgets.isEmpty()) {
            EmptyState(
                title = "暂无预算设置",
                subtitle = "点击右下角按钮添加预算",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AnimatedVisibility(visible = true, enter = fadeIn()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.budgets) { budget ->
                        BudgetCard(
                            budget = budget,
                            onDelete = { viewModel.deleteBudget(budget) },
                            onClick = { onBudgetClick(budget.id) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            title = { Text("添加预算") },
            text = {
                Column {
                    Text("选择分类", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        prefix = { Text("¥") },
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
    budget: cn.yajienet.huanaer.data.model.Budget,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = extendedColorScheme()
    val progress = (budget.spent / budget.amount).toFloat().coerceIn(0f, 1f)
    val isOverBudget = budget.spent > budget.amount
    val progressColor = when {
        progress >= 1f -> colors.budgetDanger
        progress >= 0.8f -> colors.budgetWarning
        else -> colors.income
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isOverBudget)
                colors.budgetDanger.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryCircleIcon(
                        name = budget.categoryName,
                        color = budget.categoryColor,
                        size = 36.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        budget.categoryName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "已用: ${CurrencyFormat.format(budget.spent)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOverBudget) colors.budgetDanger else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "预算: ${CurrencyFormat.format(budget.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            if (isOverBudget) {
                Text(
                    "超出预算 ${CurrencyFormat.format(budget.spent - budget.amount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.budgetDanger
                )
            } else {
                Text(
                    "剩余 ${CurrencyFormat.format(budget.amount - budget.spent)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.income
                )
            }
        }
    }
}