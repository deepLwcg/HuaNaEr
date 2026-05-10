package cn.yajienet.huanaer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cn.yajienet.huanaer.ui.screens.home.HomeScreen
import cn.yajienet.huanaer.ui.screens.statistics.StatisticsScreen
import cn.yajienet.huanaer.ui.screens.category.CategoryManageScreen
import cn.yajienet.huanaer.ui.screens.budget.BudgetListScreen
import cn.yajienet.huanaer.ui.screens.budget.BudgetDetailScreen
import cn.yajienet.huanaer.ui.screens.transaction.AddTransactionScreen
import cn.yajienet.huanaer.ui.screens.transaction.TransactionDetailScreen
import cn.yajienet.huanaer.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String,
    val title: String
)

@OptIn(ExperimentalFoundationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HuaNaErNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var addBudgetTrigger by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val bottomNavItems = remember {
        listOf(
            BottomNavItem(Screen.Home, Icons.Filled.Home, "首页", "花哪儿"),
            BottomNavItem(Screen.Statistics, Icons.Filled.BarChart, "统计", "统计分析"),
            BottomNavItem(Screen.BudgetList, Icons.Filled.AccountBalanceWallet, "预算", "预算管理"),
            BottomNavItem(Screen.Settings, Icons.Filled.Settings, "设置", "设置")
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { bottomNavItems.size })

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 主屏幕路由集合
    val mainRoutes = remember { setOf("main") }
    val isMainScreen = currentRoute == null || currentRoute in mainRoutes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            if (isMainScreen) {
                TopAppBar(
                    title = { Text(bottomNavItems[pagerState.currentPage].title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        bottomBar = {
            if (isMainScreen) {
                BottomNavBar(
                    items = bottomNavItems,
                    selectedIndex = pagerState.currentPage,
                    onItemClick = { index ->
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (isMainScreen) {
                when (pagerState.currentPage) {
                    0 -> {
                        FloatingActionButton(
                            onClick = { navController.navigate(Screen.AddTransaction.route) },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "添加交易")
                        }
                    }
                    2 -> {
                        FloatingActionButton(
                            onClick = { addBudgetTrigger++ },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "添加预算")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            // 主屏幕 - 使用单一路由，包含 HorizontalPager
            composable("main") {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 1,  // 预加载相邻页面，减少滑动卡顿
                    pageSpacing = 0.dp
                ) { page ->
                    when (page) {
                        0 -> HomeScreen(
                            contentPadding = innerPadding,
                            onAddTransactionClick = { navController.navigate(Screen.AddTransaction.route) },
                            onTransactionClick = { transactionId ->
                                navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                            }
                        )
                        1 -> StatisticsScreen(contentPadding = innerPadding)
                        2 -> BudgetListScreen(
                            contentPadding = innerPadding,
                            onBudgetClick = { budgetId ->
                                navController.navigate(Screen.BudgetDetail.createRoute(budgetId))
                            },
                            addBudgetTrigger = addBudgetTrigger
                        )
                        3 -> SettingsScreen(
                            contentPadding = innerPadding,
                            onNavigateToCategoryManage = { navController.navigate(Screen.CategoryManage.route) }
                        )
                    }
                }
            }

            // 子屏幕
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.TransactionDetail.route) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId")?.toLongOrNull()
                if (transactionId != null) {
                    TransactionDetailScreen(
                        transactionId = transactionId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    Text("无效的交易ID")
                }
            }
            composable(Screen.BudgetDetail.route) { backStackEntry ->
                val budgetId = backStackEntry.arguments?.getString("budgetId")?.toLongOrNull()
                if (budgetId != null) {
                    BudgetDetailScreen(
                        budgetId = budgetId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    Text("无效的预算ID")
                }
            }
            composable(Screen.CategoryManage.route) {
                CategoryManageScreen(
                    contentPadding = PaddingValues(0.dp),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { index, item ->
                val selected = selectedIndex == index
                NavBarItem(
                    item = item,
                    selected = selected,
                    onClick = { onItemClick(index) }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (selected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
            )
        }
        Spacer(Modifier.height(0.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}