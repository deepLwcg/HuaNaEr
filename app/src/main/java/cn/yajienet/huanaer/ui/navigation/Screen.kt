package cn.yajienet.huanaer.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "首页")
    object TransactionList : Screen("transactions", "交易")
    object AddTransaction : Screen("transactions/add", "添加交易")
    object TransactionDetail : Screen("transactions/{transactionId}", "交易详情") {
        fun createRoute(transactionId: Long) = "transactions/$transactionId"
    }
    object Statistics : Screen("statistics", "统计")
    object CategoryManage : Screen("categories", "分类")
    object BudgetList : Screen("budgets", "预算")
    object AddBudget : Screen("budgets/add", "添加预算")
    object BudgetDetail : Screen("budgets/{budgetId}", "预算详情") {
        fun createRoute(budgetId: Long) = "budgets/$budgetId"
    }
    object Settings : Screen("settings", "设置")
}