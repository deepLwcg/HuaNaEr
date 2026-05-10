package cn.yajienet.huanaer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String,
    val title: String
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HuaNaErNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var addBudgetTrigger by remember { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, Icons.Filled.Home, "首页", "花哪儿"),
        BottomNavItem(Screen.Statistics, Icons.Filled.BarChart, "统计", "统计分析"),
        BottomNavItem(Screen.BudgetList, Icons.Filled.AccountBalanceWallet, "预算", "预算管理"),
        BottomNavItem(Screen.CategoryManage, Icons.Filled.Category, "分类", "分类管理")
    )

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.screen.route }
    val currentNavItem = bottomNavItems.find { it.screen.route == currentDestination?.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            if (showBottomBar && currentNavItem != null) {
                TopAppBar(
                    title = { Text(currentNavItem.title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            when (currentDestination?.route) {
                Screen.Home.route -> {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.AddTransaction.route) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "添加交易")
                    }
                }
                Screen.BudgetList.route -> {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
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
            composable(Screen.Home.route) {
                HomeScreen(
                    contentPadding = innerPadding,
                    onAddTransactionClick = {
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onTransactionClick = { transactionId ->
                        navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                    }
                )
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(
                    contentPadding = innerPadding
                )
            }
            composable(Screen.CategoryManage.route) {
                CategoryManageScreen(
                    contentPadding = innerPadding
                )
            }
            composable(Screen.BudgetList.route) {
                BudgetListScreen(
                    contentPadding = innerPadding,
                    onBudgetClick = { budgetId ->
                        navController.navigate(Screen.BudgetDetail.createRoute(budgetId))
                    },
                    addBudgetTrigger = addBudgetTrigger
                )
            }
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
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
        }
    }
}