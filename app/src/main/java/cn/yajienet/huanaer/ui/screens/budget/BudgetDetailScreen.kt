package cn.yajienet.huanaer.ui.screens.budget

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.glassmorphism.BudgetRing
import cn.yajienet.huanaer.ui.components.glassmorphism.GlassCard
import cn.yajienet.huanaer.ui.components.glassmorphism.NeumorphicCard
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
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

    // 保存或删除后自动返回
    LaunchedEffect(uiState.saved, uiState.deleted) {
        if (uiState.saved || uiState.deleted) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预算详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.budget != null) {
            val budget = uiState.budget!!
            val progress = if (budget.amount > 0) (budget.spent / budget.amount).toFloat().coerceIn(0f, 1f) else 0f
            val isOverBudget = budget.spent > budget.amount
            val isWarning = progress >= 0.8f && !isOverBudget

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 头部 GlassCard + 大型环形进度
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
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
                            size = 48.dp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            budget.categoryName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(16.dp))

                        // 大型环形进度
                        BudgetRing(
                            progress = progress,
                            color = when {
                                isOverBudget -> colors.budgetDanger
                                isWarning -> colors.budgetWarning
                                else -> MaterialTheme.colorScheme.primary
                            },
                            size = 160.dp,
                            strokeWidth = 10.dp
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("已用", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    CurrencyFormat.format(budget.spent),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = when {
                                        isOverBudget -> colors.budgetDanger
                                        isWarning -> colors.budgetWarning
                                        else -> colors.expense
                                    }
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("预算", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    CurrencyFormat.format(budget.amount),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("剩余", style = MaterialTheme.typography.labelMedium)
                                val remaining = budget.amount - budget.spent
                                Text(
                                    CurrencyFormat.format(remaining),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (remaining < 0) colors.budgetDanger else colors.income
                                )
                            }
                        }
                    }
                }

                // 快捷操作
                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 编辑按钮
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { viewModel.startEditing() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "编辑",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        // 删除按钮
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { showDeleteDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // 编辑底部弹窗
    if (uiState.isEditing) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.cancelEditing() },
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
                    "编辑预算",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { viewModel.setAmount(it) },
                    label = { Text("预算金额") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                uiState.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = { viewModel.cancelEditing() },
                        modifier = Modifier.weight(1f)
                    ) { Text("取消") }
                    Button(
                        onClick = { viewModel.saveBudget() },
                        modifier = Modifier.weight(1f),
                        enabled = uiState.amount.toDoubleOrNull()?.let { it > 0 } == true
                    ) { Text("保存") }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Filled.Delete, "删除") },
            title = { Text("删除预算") },
            text = { Text("确定要删除该预算吗？此操作不可恢复。") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBudget()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("删除") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("取消") }
            }
        )
    }
}