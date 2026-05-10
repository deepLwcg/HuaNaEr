package cn.yajienet.huanaer.ui.screens.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
import cn.yajienet.huanaer.ui.components.MonthYearSelector
import cn.yajienet.huanaer.ui.components.TransactionListItem
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat
import cn.yajienet.huanaer.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onTransactionClick: (Long) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: TransactionListViewModel = viewModel(
        factory = TransactionListViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()
    val colors = extendedColorScheme()

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var expandedItemId by remember { mutableStateOf<Long?>(null) }
    val lazyListState = rememberLazyListState()

    // 滚动时收回所有展开的删除按钮
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress) {
            expandedItemId = null
        }
    }

    if (uiState.isLoading) {
        LoadingState()
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                TopAppBar(
                    title = { Text("交易记录") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = uiState.selectionMode == TimeSelectionMode.MONTH,
                        onClick = { viewModel.setSelectionMode(TimeSelectionMode.MONTH) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 0,
                            count = 2
                        )
                    ) {
                        Text("按月")
                    }
                    SegmentedButton(
                        selected = uiState.selectionMode == TimeSelectionMode.CUSTOM,
                        onClick = { viewModel.setSelectionMode(TimeSelectionMode.CUSTOM) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 1,
                            count = 2
                        )
                    ) {
                        Text("自定义")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(
                    visible = uiState.selectionMode == TimeSelectionMode.MONTH,
                    enter = fadeIn()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MonthYearSelector(
                            year = uiState.selectedYear,
                            month = uiState.selectedMonth,
                            onPrevious = { viewModel.previousMonth() },
                            onNext = { viewModel.nextMonth() },
                            onDateSelected = { year, month -> viewModel.setDate(year, month) }
                        )
                    }
                }

                AnimatedVisibility(
                    visible = uiState.selectionMode == TimeSelectionMode.CUSTOM,
                    enter = fadeIn()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 占位，保持与 MonthYearSelector 高度一致
                        Box(modifier = Modifier.size(48.dp)) { }
                        Text(
                            text = DateUtils.formatDate(uiState.startDate),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable { showStartDatePicker = true }
                        )
                        Text(
                            text = "至",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = DateUtils.formatDate(uiState.endDate),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable { showEndDatePicker = true }
                        )
                        // 占位，保持与 MonthYearSelector 高度一致
                        Box(modifier = Modifier.size(48.dp)) { }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
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

                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

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

                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "结余",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormat.format(uiState.balance),
                                style = MaterialTheme.typography.titleLarge,
                                color = if (uiState.balance >= 0) colors.income else colors.expense,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.transactions.isEmpty()) {
                    EmptyState(
                        title = "暂无交易记录",
                        subtitle = "当前时间段内没有交易",
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        state = lazyListState,
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.transactions) { transaction ->
                            TransactionListItem(
                                transaction = transaction,
                                onClick = { onTransactionClick(transaction.id) },
                                onDelete = { viewModel.deleteTransaction(transaction) },
                                isExpanded = expandedItemId == transaction.id,
                                onExpand = { expandedItemId = transaction.id }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.startDate)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { start ->
                            val end = uiState.endDate
                            if (start <= end) {
                                viewModel.setCustomDateRange(start, end)
                            }
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.endDate)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { end ->
                            val start = uiState.startDate
                            if (start <= end) {
                                viewModel.setCustomDateRange(start, end)
                            }
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}