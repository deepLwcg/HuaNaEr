package cn.yajienet.huanaer.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
    val label: String
)

@Composable
fun HuaNaErNavigation(
    modifier: Modifier = Modifier,
    onNavigateToAddTransaction: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, Icons.Filled.Home, "首页"),
        BottomNavItem(Screen.Statistics, Icons.Filled.BarChart, "统计"),
        BottomNavItem(Screen.BudgetList, Icons.Filled.AccountBalanceWallet, "预算"),
        BottomNavItem(Screen.CategoryManage, Icons.Filled.Category, "分类")
    )

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.screen.route }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
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
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddTransactionClick = {
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onTransactionClick = { transactionId ->
                        navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                    },
                    onNavigateToStatistics = {
                        navController.navigate(Screen.Statistics.route)
                    },
                    onNavigateToBudget = {
                        navController.navigate(Screen.BudgetList.route)
                    }
                )
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }
            composable(Screen.CategoryManage.route) {
                CategoryManageScreen()
            }
            composable(Screen.BudgetList.route) {
                BudgetListScreen(
                    onAddBudgetClick = {
                        navController.navigate(Screen.AddBudget.route)
                    },
                    onBudgetClick = { budgetId ->
                        navController.navigate(Screen.BudgetDetail.createRoute(budgetId))
                    }
                )
            }
            // Additional screens will be added later
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
            composable(Screen.AddBudget.route) {
                // Placeholder - will be implemented
                Text("添加预算")
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