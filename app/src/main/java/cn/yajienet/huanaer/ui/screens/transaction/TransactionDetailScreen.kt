package cn.yajienet.huanaer.ui.screens.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.glassmorphism.GlassCard
import cn.yajienet.huanaer.ui.components.candy.CandyCard
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
    LaunchedEffect(uiState.saved, uiState.deleted) {
        if (uiState.saved || uiState.deleted) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditing) "编辑交易" else "交易详情",
                        fontWeight = FontWeight.Medium
                    )
                },
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
                            Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.isEditing && uiState.transaction != null) {
                FloatingActionButton(
                    onClick = { viewModel.saveTransaction() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Check, "保存")
                    }
                }
            }
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Type selection
                Column {
                    Text(
                        "交易类型",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = uiState.type == TransactionType.EXPENSE,
                            onClick = { viewModel.setType(TransactionType.EXPENSE) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("- ", color = colors.expense, fontWeight = FontWeight.Bold)
                                    Text("支出")
                                }
                            }
                        )
                        FilterChip(
                            selected = uiState.type == TransactionType.INCOME,
                            onClick = { viewModel.setType(TransactionType.INCOME) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("+ ", color = colors.income, fontWeight = FontWeight.Bold)
                                    Text("收入")
                                }
                            }
                        )
                    }
                }

                // Amount input - 大号输入框
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { viewModel.setAmount(it) },
                    label = { Text("金额") },
                    prefix = {
                        Text(
                            "¥",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (uiState.type == TransactionType.EXPENSE) colors.expense else colors.income
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.error != null && uiState.amount.isBlank(),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                // Category selection
                Column {
                    Text(
                        "选择分类",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.categories.forEach { category ->
                            FilterChip(
                                selected = uiState.selectedCategoryId == category.id,
                                onClick = { viewModel.setCategory(category.id) },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CategoryCircleIcon(
                                            name = category.name,
                                            color = category.color,
                                            size = 18.dp
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(category.name)
                                    }
                                }
                            )
                        }
                    }
                }

                // Date picker field
                OutlinedTextField(
                    value = DateUtils.formatDate(uiState.date),
                    onValueChange = {},
                    label = { Text("日期") },
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Default.CalendarMonth, "日期")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                // Note input
                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = { viewModel.setNote(it) },
                    label = { Text("备注（可选）") },
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Note, "备注")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2
                )

                // Error display
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(80.dp)) // FAB space
            }
        } else {
            // View mode - 新设计
            val transaction = uiState.transaction!!
            val isExpense = transaction.type == TransactionType.EXPENSE
            val primaryColor = if (isExpense) colors.expense else colors.income
            val backgroundColor = if (isExpense) colors.expenseContainer else colors.incomeContainer

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // === 顶部金额展示区域（柔和渐变）===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    backgroundColor.copy(alpha = 0.15f),
                                    backgroundColor.copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = 200f
                            )
                        )
                        .padding(top = 24.dp, bottom = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 类型标签（小胶囊样式）
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(primaryColor.copy(alpha = 0.15f))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isExpense) "支出" else "收入",
                                style = MaterialTheme.typography.labelMedium,
                                color = primaryColor,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // 金额显示（超大号，带符号）
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isExpense) "-" else "+",
                                style = MaterialTheme.typography.displaySmall,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = CurrencyFormat.format(transaction.amount).removePrefix("¥"),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = primaryColor
                            )
                        }
                    }
                }

                // === 分类卡片（GlassCard）===
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 分类图标（大号）
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CategoryCircleIcon(
                                    name = transaction.categoryName,
                                    color = transaction.categoryColor,
                                    size = 40.dp
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "分类",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = transaction.categoryName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // === 详情信息卡片（CandyCard）===
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 3 }
                ) {
                    CandyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // 日期
                            InfoItem(
                                icon = Icons.Default.CalendarMonth,
                                iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                iconTintColor = MaterialTheme.colorScheme.primary,
                                label = "日期",
                                value = DateUtils.formatDate(transaction.date)
                            )

                            Spacer(Modifier.height(16.dp))

                            // 备注（如果有）
                            if (transaction.note != null && transaction.note.isNotBlank()) {
                                InfoItem(
                                    icon = Icons.AutoMirrored.Filled.Note,
                                    iconBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                    iconTintColor = MaterialTheme.colorScheme.secondary,
                                    label = "备注",
                                    value = transaction.note
                                )
                                Spacer(Modifier.height(16.dp))
                            }

                            // 创建时间
                            InfoItem(
                                icon = Icons.Default.Schedule,
                                iconBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                iconTintColor = MaterialTheme.colorScheme.tertiary,
                                label = "创建时间",
                                value = DateUtils.formatDateTime(transaction.createdAt)
                            )
                        }
                    }
                }

                // Error display
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("删除交易") },
            text = { Text("确定要删除这条交易记录吗？删除后无法恢复。") },
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
fun InfoItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTintColor: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标（带背景圆圈）
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = iconTintColor
            )
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}