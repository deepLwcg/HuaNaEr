package cn.yajienet.huanaer.ui.screens.statistics

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.CategoryRepository
import cn.yajienet.huanaer.data.repository.TransactionRepository
import cn.yajienet.huanaer.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class CategoryStatistics(
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: String,
    val amount: Double,
    val percentage: Float
)

data class StatisticsUiState(
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val expenseByCategory: List<CategoryStatistics> = emptyList(),
    val incomeByCategory: List<CategoryStatistics> = emptyList(),
    val selectedMonth: Int = DateUtils.getCurrentMonth(),
    val selectedYear: Int = DateUtils.getCurrentYear(),
    val isLoading: Boolean = true
)

class StatisticsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(DateUtils.getCurrentMonth())
    private val _selectedYear = MutableStateFlow(DateUtils.getCurrentYear())

    // 计算时间范围
    private val dateRange = combine(_selectedMonth, _selectedYear) { month, year ->
        DateUtils.getMonthStartTime(month, year) to DateUtils.getMonthEndTime(month, year)
    }

    // 使用 flatMapLatest 动态切换 Flow，当月份变化时自动重新订阅
    val uiState: StateFlow<StatisticsUiState> = dateRange.flatMapLatest { (startTime, endTime) ->
        combine(
            categoryRepository.getAll(),
            transactionRepository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, startTime, endTime),
            transactionRepository.getTotalByTypeAndDateRange(TransactionType.INCOME, startTime, endTime),
            transactionRepository.getCategoryTotals(TransactionType.EXPENSE, startTime, endTime),
            transactionRepository.getCategoryTotals(TransactionType.INCOME, startTime, endTime)
        ) { categories, totalExpense, totalIncome, expenseTotals, incomeTotals ->
            val expenseStats = expenseTotals.map { (categoryId, amount) ->
                val category = categories.find { it.id == categoryId }
                CategoryStatistics(
                    categoryId = categoryId,
                    categoryName = category?.name ?: "未知",
                    categoryColor = category?.color ?: "#808080",
                    amount = amount,
                    percentage = if (totalExpense > 0) (amount / totalExpense).toFloat() else 0f
                )
            }.sortedByDescending { it.amount }

            val incomeStats = incomeTotals.map { (categoryId, amount) ->
                val category = categories.find { it.id == categoryId }
                CategoryStatistics(
                    categoryId = categoryId,
                    categoryName = category?.name ?: "未知",
                    categoryColor = category?.color ?: "#808080",
                    amount = amount,
                    percentage = if (totalIncome > 0) (amount / totalIncome).toFloat() else 0f
                )
            }.sortedByDescending { it.amount }

            StatisticsUiState(
                totalExpense = totalExpense,
                totalIncome = totalIncome,
                expenseByCategory = expenseStats,
                incomeByCategory = incomeStats,
                selectedMonth = _selectedMonth.value,
                selectedYear = _selectedYear.value,
                isLoading = false
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsUiState(isLoading = true))

    fun previousMonth() {
        val currentMonth = _selectedMonth.value
        val currentYear = _selectedYear.value
        _selectedMonth.value = if (currentMonth == 1) 12 else currentMonth - 1
        _selectedYear.value = if (currentMonth == 1) currentYear - 1 else currentYear
    }

    fun nextMonth() {
        val currentMonth = _selectedMonth.value
        val currentYear = _selectedYear.value
        _selectedMonth.value = if (currentMonth == 12) 1 else currentMonth + 1
        _selectedYear.value = if (currentMonth == 12) currentYear + 1 else currentYear
    }
}

class StatisticsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as cn.yajienet.huanaer.HuaNaErApplication
            return StatisticsViewModel(app.transactionRepository, app.categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}