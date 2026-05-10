package cn.yajienet.huanaer.ui.screens.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.util.CurrencyFormat
import cn.yajienet.huanaer.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Long,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: TransactionDetailViewModel = viewModel(
        factory = TransactionDetailViewModelFactory(application, transactionId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val colors = extendedColorScheme()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.date
    )

    // Navigate back when saved or deleted
    if (uiState.saved || uiState.deleted) {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "编辑交易" else "交易详情") },
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
                    if (!uiState.isEditing && uiState.transaction != null) {
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
        } else if (uiState.transaction == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("交易不存在", style = MaterialTheme.typography.bodyLarge)
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
                // Type selection
                Column {
                    Text("类型", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.type == TransactionType.EXPENSE,
                            onClick = { viewModel.setType(TransactionType.EXPENSE) },
                            label = { Text("支出") }
                        )
                        FilterChip(
                            selected = uiState.type == TransactionType.INCOME,
                            onClick = { viewModel.setType(TransactionType.INCOME) },
                            label = { Text("收入") }
                        )
                    }
                }

                // Amount input
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { viewModel.setAmount(it) },
                    label = { Text("金额") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.error != null && uiState.amount.isBlank()
                )

                // Category selection
                Column {
                    Text("分类", style = MaterialTheme.typography.labelLarge)
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
                }

                // Note input
                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = { viewModel.setNote(it) },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date picker field
                OutlinedTextField(
                    value = DateUtils.formatDate(uiState.date),
                    onValueChange = {},
                    label = { Text("日期") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, "选择日期")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                // Save button
                Button(
                    onClick = { viewModel.saveTransaction() },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("保存中...")
                    } else {
                        Text("保存")
                    }
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
                val transaction = uiState.transaction!!

                // Amount card
                val amountColor = when (transaction.type) {
                    TransactionType.EXPENSE -> colors.expense
                    TransactionType.INCOME -> colors.income
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (transaction.type) {
                            TransactionType.EXPENSE -> colors.expenseContainer.copy(alpha = 0.3f)
                            TransactionType.INCOME -> colors.incomeContainer.copy(alpha = 0.3f)
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (transaction.type == TransactionType.EXPENSE) "支出" else "收入",
                            style = MaterialTheme.typography.labelLarge,
                            color = amountColor
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = CurrencyFormat.format(transaction.amount),
                            style = MaterialTheme.typography.headlineMedium,
                            color = amountColor
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Category
                        DetailRow(
                            label = "分类",
                            value = transaction.categoryName,
                            leadingIcon = {
                                CategoryCircleIcon(
                                    name = transaction.categoryName,
                                    color = transaction.categoryColor,
                                    size = 32.dp,
                                    textStyle = MaterialTheme.typography.labelMedium
                                )
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        // Date
                        DetailRow(
                            label = "日期",
                            value = DateUtils.formatDate(transaction.date)
                        )

                        // Note (if exists)
                        if (transaction.note != null && transaction.note.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))
                            DetailRow(
                                label = "备注",
                                value = transaction.note
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Created time
                        DetailRow(
                            label = "创建时间",
                            value = DateUtils.formatDateTime(transaction.createdAt)
                        )
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
            title = { Text("删除交易") },
            text = { Text("确定要删除这条交易记录吗？此操作无法撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteTransaction()
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

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { date ->
                            viewModel.setDate(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(12.dp))
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}