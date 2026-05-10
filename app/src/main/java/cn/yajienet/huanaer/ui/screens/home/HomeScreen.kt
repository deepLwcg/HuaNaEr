package cn.yajienet.huanaer.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.ui.components.EmptyState
import cn.yajienet.huanaer.ui.components.LoadingState
import cn.yajienet.huanaer.ui.components.SummaryCard
import cn.yajienet.huanaer.ui.components.TransactionListItem
@Composable
fun HomeScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onAddTransactionClick: () -> Unit = {},
    onTransactionClick: (Long) -> Unit = {},
    onNavigateToTransactionList: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    SummaryCard(
                        totalIncome = uiState.totalIncome,
                        totalExpense = uiState.totalExpense,
                        balance = uiState.balance
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
                    title = "暂无交易记录",
                    subtitle = "点击右下角按钮开始记账",
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
                    items(uiState.recentTransactions) { transaction ->
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