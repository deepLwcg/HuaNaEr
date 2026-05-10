package cn.yajienet.huanaer

import android.app.Application
import cn.yajienet.huanaer.data.local.database.HuaNaErDatabase
import cn.yajienet.huanaer.data.datastore.SettingsDataStore
import cn.yajienet.huanaer.data.local.entity.CategoryEntity
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.BudgetRepository
import cn.yajienet.huanaer.data.repository.CategoryRepository
import cn.yajienet.huanaer.data.repository.SettingsRepository
import cn.yajienet.huanaer.data.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HuaNaErApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: HuaNaErDatabase by lazy {
        HuaNaErDatabase.getDatabase(this)
    }

    val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(this)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(settingsDataStore)
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(transactionDao = database.transactionDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(categoryDao = database.categoryDao())
    }

    val budgetRepository: BudgetRepository by lazy {
        BudgetRepository(
            budgetDao = database.budgetDao(),
            transactionDao = database.transactionDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            initDefaultCategories()
        }
    }

    private suspend fun initDefaultCategories() {
        val existingCategories = categoryRepository.getAll()
        existingCategories.collect { categories ->
            if (categories.isEmpty()) {
                val defaultExpenseCategories = listOf(
                    CategoryEntity(name = "餐饮", icon = "restaurant", color = "#FF6B6B", type = TransactionType.EXPENSE, sortOrder = 1, isDefault = true),
                    CategoryEntity(name = "交通", icon = "directions_car", color = "#4ECDC4", type = TransactionType.EXPENSE, sortOrder = 2, isDefault = true),
                    CategoryEntity(name = "购物", icon = "shopping_cart", color = "#45B7D1", type = TransactionType.EXPENSE, sortOrder = 3, isDefault = true),
                    CategoryEntity(name = "娱乐", icon = "movie", color = "#96CEB4", type = TransactionType.EXPENSE, sortOrder = 4, isDefault = true),
                    CategoryEntity(name = "医疗", icon = "local_hospital", color = "#FFEAA7", type = TransactionType.EXPENSE, sortOrder = 5, isDefault = true),
                    CategoryEntity(name = "教育", icon = "school", color = "#DDA0DD", type = TransactionType.EXPENSE, sortOrder = 6, isDefault = true),
                    CategoryEntity(name = "住房", icon = "home", color = "#98D8C8", type = TransactionType.EXPENSE, sortOrder = 7, isDefault = true),
                    CategoryEntity(name = "其他", icon = "more_horiz", color = "#808080", type = TransactionType.EXPENSE, sortOrder = 8, isDefault = true)
                )

                val defaultIncomeCategories = listOf(
                    CategoryEntity(name = "工资", icon = "account_balance_wallet", color = "#4CAF50", type = TransactionType.INCOME, sortOrder = 1, isDefault = true),
                    CategoryEntity(name = "奖金", icon = "card_giftcard", color = "#8BC34A", type = TransactionType.INCOME, sortOrder = 2, isDefault = true),
                    CategoryEntity(name = "投资", icon = "trending_up", color = "#CDDC39", type = TransactionType.INCOME, sortOrder = 3, isDefault = true),
                    CategoryEntity(name = "兼职", icon = "work", color = "#FFEB3B", type = TransactionType.INCOME, sortOrder = 4, isDefault = true),
                    CategoryEntity(name = "其他", icon = "more_horiz", color = "#808080", type = TransactionType.INCOME, sortOrder = 5, isDefault = true)
                )

                defaultExpenseCategories.forEach { categoryRepository.insert(it) }
                defaultIncomeCategories.forEach { categoryRepository.insert(it) }
            }
        }
    }
}