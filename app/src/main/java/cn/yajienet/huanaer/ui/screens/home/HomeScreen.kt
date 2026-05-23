package cn.yajienet.huanaer.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
import cn.yajienet.huanaer.ui.components.SummaryCard
import cn.yajienet.huanaer.ui.components.TransactionListItem
import cn.yajienet.huanaer.ui.components.glassmorphism.GlowBackground

@Composable
fun HomeScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onAddTransactionClick: () -> Unit = {},
    onTransactionClick: (Long) -> Unit = {},
    onNavigateToTransactionList: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(application))
    val uiState by viewModel.uiState.collectAsState()
    var expandedItemId by remember { mutableStateOf<Long?>(null) }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collect { if (it) expandedItemId = null }
    }

    GlowBackground(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            LoadingState()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AnimatedVisibility(
                    visible = !uiState.isLoading,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        SummaryCard(
                            totalBalance = uiState.balance,
                            totalIncome = uiState.totalIncome,
                            totalExpense = uiState.totalExpense,
                            dailyExpenses = uiState.dailyExpenses
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "最近交易",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onNavigateToTransactionList) {
                        Text("查看全部")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.recentTransactions.isEmpty()) {
                    EmptyState(
                        title = "还没有记录，开始记一笔吧～",
                        subtitle = "点击下方按钮快速记账",
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        state = lazyListState,
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.recentTransactions,
                            key = { it.id }
                        ) { transaction ->
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
}