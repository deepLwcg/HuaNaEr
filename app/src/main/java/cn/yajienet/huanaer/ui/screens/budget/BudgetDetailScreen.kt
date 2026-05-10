package cn.yajienet.huanaer.ui.screens.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.util.CurrencyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreen(
    budgetId: Long,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: BudgetDetailViewModel = viewModel(
        factory = BudgetDetailViewModelFactory(application, budgetId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val colors = extendedColorScheme()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Navigate back when saved or deleted
    if (uiState.saved || uiState.deleted) {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "编辑预算" else "预算详情") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.isEditing) {
                            viewModel.cancelEditing()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    if (!uiState.isEditing && uiState.budget != null) {
                        IconButton(onClick = { viewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, "编辑")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "删除")
                        }
                    }
                }
            )
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
        } else if (uiState.budget == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("预算不存在", style = MaterialTheme.typography.bodyLarge)
            }
        } else if (uiState.isEditing) {
            // Edit mode
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val budget = uiState.budget!!

                // Category info (non-editable)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryCircleIcon(
                            name = budget.categoryName,
                            color = budget.categoryColor,
                            size = 40.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = budget.categoryName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${budget.year}年${budget.month}月",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Amount input
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { viewModel.setAmount(it) },
                    label = { Text("预算金额") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.error != null && uiState.amount.isBlank(),
                    prefix = { Text("¥") }
                )

                // Save button
                Button(
                    onClick = { viewModel.saveBudget() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("保存")
                }

                // Error display
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // View mode
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                val budget = uiState.budget!!
                val progress = (budget.spent / budget.amount).toFloat().coerceIn(0f, 1f)
                val isOverBudget = budget.spent > budget.amount
                val remaining = budget.amount - budget.spent
                val progressColor = when {
                    progress >= 1f -> colors.budgetDanger
                    progress >= 0.8f -> colors.budgetWarning
                    else -> colors.income
                }

                // Category and amount header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOverBudget)
                            colors.budgetDangerContainer.copy(alpha = 0.3f)
                        else
                            colors.incomeContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CategoryCircleIcon(
                            name = budget.categoryName,
                            color = budget.categoryColor,
                            size = 56.dp,
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = budget.categoryName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${budget.year}年${budget.month}月",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "预算: ${CurrencyFormat.format(budget.amount)}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Progress card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "已支出",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormat.format(budget.spent),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isOverBudget) colors.budgetDanger else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isOverBudget) "超出" else "剩余",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormat.format(if (isOverBudget) budget.spent - budget.amount else remaining),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isOverBudget) colors.budgetDanger else colors.income
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = progressColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "${(progress * 100).toInt()}% 已使用",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status message
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOverBudget)
                            colors.budgetDangerContainer.copy(alpha = 0.3f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isOverBudget) {
                            Text(
                                text = "⚠️ 预算已超出 ${CurrencyFormat.format(budget.spent - budget.amount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.budgetDanger
                            )
                        } else {
                            Text(
                                text = "✓ 预算正常，还有 ${CurrencyFormat.format(remaining)} 可用",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.income
                            )
                        }
                    }
                }

                // Error display
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除预算") },
            text = { Text("确定要删除这个预算吗？此操作无法撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteBudget()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}