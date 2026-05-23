package cn.yajienet.huanaer.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cn.yajienet.huanaer.ui.screens.home.HomeScreen
import cn.yajienet.huanaer.ui.screens.statistics.StatisticsScreen
import cn.yajienet.huanaer.ui.screens.statistics.StatisticsViewModel
import cn.yajienet.huanaer.ui.screens.statistics.StatisticsViewModelFactory
import cn.yajienet.huanaer.ui.screens.category.CategoryManageScreen
import cn.yajienet.huanaer.ui.screens.budget.BudgetListScreen
import cn.yajienet.huanaer.ui.screens.budget.BudgetDetailScreen
import cn.yajienet.huanaer.ui.screens.transaction.AddTransactionScreen
import cn.yajienet.huanaer.ui.screens.transaction.AddTransactionBottomSheet
import cn.yajienet.huanaer.ui.screens.transactionlist.TransactionListScreen
import cn.yajienet.huanaer.ui.screens.transaction.TransactionDetailScreen
import cn.yajienet.huanaer.ui.screens.settings.SettingsScreen
import cn.yajienet.huanaer.ui.components.glassmorphism.GlassCard
import cn.yajienet.huanaer.ui.components.glassmorphism.GlowBackground
import cn.yajienet.huanaer.ui.components.MonthYearPickerDialog
import cn.yajienet.huanaer.ui.theme.EaseInOutCubic
import cn.yajienet.huanaer.util.DateUtils
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
    modifier: Modifier = Modifier,
    initialRoute: String? = null
) {
    val navController = rememberNavController()
    var addBudgetTrigger by remember { mutableIntStateOf(0) }
    var showAddTransaction by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 处理快捷方式的初始路由
    LaunchedEffect(initialRoute) {
        if (initialRoute != null) {
            navController.navigate(initialRoute)
        }
    }

    // 统计页面的ViewModel，在TopAppBar中共享
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(application)
    )
    val statisticsUiState by statisticsViewModel.uiState.collectAsState()
    var showMonthYearPicker by remember { mutableStateOf(false) }

    val bottomNavItems = remember {
        listOf(
            BottomNavItem(Screen.Home, Icons.Filled.Home, "首页", "花哪儿了"),
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AnimatedVisibility(
                visible = isMainScreen,
                enter = fadeIn(
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ),
                exit = fadeOut(
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                )
            ) {
                TopAppBar(
                    title = { Text(bottomNavItems[pagerState.currentPage].title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        if (pagerState.currentPage == 0) {
                            IconButton(
                                onClick = { showAddTransaction = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "添加交易",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else if (pagerState.currentPage == 1) {
                            Text(
                                text = DateUtils.formatMonthYear(
                                    statisticsUiState.selectedMonth,
                                    statisticsUiState.selectedYear
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { showMonthYearPicker = true }
                            )
                        } else if (pagerState.currentPage == 2) {
                            IconButton(
                                onClick = { addBudgetTrigger++ }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "添加预算",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isMainScreen,
                enter = fadeIn(
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ),
                exit = fadeOut(
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                )
            ) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300, easing = EaseInOutCubic)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300, easing = EaseInOutCubic)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300, easing = EaseInOutCubic)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300, easing = EaseInOutCubic)
                )
            }
        ) {
            // 主屏幕 - 使用单一路由，包含 HorizontalPager
            composable("main") {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 1,
                    pageSpacing = 0.dp
                ) { page ->
                    when (page) {
                        0 -> HomeScreen(
                            contentPadding = innerPadding,
                            onAddTransactionClick = { showAddTransaction = true },
                            onTransactionClick = { transactionId ->
                                navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                            },
                            onNavigateToTransactionList = {
                                navController.navigate(Screen.TransactionList.route)
                            }
                        )
                        1 -> StatisticsScreen(
                            contentPadding = innerPadding,
                            viewModel = statisticsViewModel
                        )
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
            composable(Screen.TransactionList.route) {
                TransactionListScreen(
                    contentPadding = PaddingValues(0.dp),
                    onTransactionClick = { transactionId ->
                        navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
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

    // 添加交易底部弹窗（从主页面触发）
    if (showAddTransaction) {
        AddTransactionBottomSheet(
            onDismiss = { showAddTransaction = false }
        )
    }

    // 统计页面月份选择对话框
    if (showMonthYearPicker) {
        MonthYearPickerDialog(
            initialYear = statisticsUiState.selectedYear,
            initialMonth = statisticsUiState.selectedMonth,
            onDismiss = { showMonthYearPicker = false },
            onConfirm = { year, month ->
                statisticsViewModel.setDate(year, month)
                showMonthYearPicker = false
            }
        )
    }
}

@Composable
private fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        glassAlpha = 0.85f,
        blurRadius = 30.dp
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
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navItemScale"
    )

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
                    else Color.Transparent,
            shape = RoundedCornerShape(16.dp),
            border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)) else null,
            modifier = Modifier
                .size(32.dp)
                .scale(scale)
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
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            maxLines = 1,
            color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
