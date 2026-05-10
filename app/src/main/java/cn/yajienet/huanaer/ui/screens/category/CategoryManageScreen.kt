package cn.yajienet.huanaer.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryManageScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("分类管理") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog(TransactionType.EXPENSE) }) {
                Icon(Icons.Filled.Add, "添加分类")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Expense Categories
            Text(
                text = "支出分类",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                FlowRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.expenseCategories.forEach { category ->
                        CategoryItem(
                            category = category,
                            onDelete = { viewModel.deleteCategory(category) }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .clickable { viewModel.showAddDialog(TransactionType.EXPENSE) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Add, "添加", tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Income Categories
            Text(
                text = "收入分类",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                FlowRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.incomeCategories.forEach { category ->
                        CategoryItem(
                            category = category,
                            onDelete = { viewModel.deleteCategory(category) }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .clickable { viewModel.showAddDialog(TransactionType.INCOME) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Add, "添加", tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    // Add Dialog
    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            title = { Text("添加${if (uiState.dialogType == TransactionType.EXPENSE) "支出" else "收入"}分类") },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.dialogName,
                        onValueChange = { viewModel.setDialogName(it) },
                        label = { Text("分类名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "颜色",
                        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val colors = listOf("#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD", "#98D8C8", "#808080")
                        colors.forEach { colorHex ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(android.graphics.Color.parseColor(colorHex)), CircleShape)
                                    .clickable { viewModel.setDialogColor(colorHex) }
                            ) {
                                if (uiState.dialogColor == colorHex) {
                                    Icon(
                                        Icons.Filled.Add,
                                        "选中",
                                        tint = Color.White,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.addCategory() }) {
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
fun CategoryItem(
    category: Category,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(android.graphics.Color.parseColor(category.color)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.name.take(2),
                color = Color.White,
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                Icons.Filled.Delete,
                "删除",
                tint = androidx.compose.material3.MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}