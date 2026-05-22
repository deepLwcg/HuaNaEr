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
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

enum class TimeRange { THIS_MONTH, LAST_3_MONTHS, LAST_6_MONTHS, THIS_YEAR }

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
    val selectedTimeRange: TimeRange = TimeRange.THIS_MONTH,
    val isLoading: Boolean = true
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class StatisticsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(DateUtils.getCurrentMonth())
    private val _selectedYear = MutableStateFlow(DateUtils.getCurrentYear())
    private val _selectedTimeRange = MutableStateFlow(TimeRange.THIS_MONTH)

    // 根据 TimeRange 计算时间范围
    private fun calcDateRange(timeRange: TimeRange): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return when (timeRange) {
            TimeRange.THIS_MONTH -> {
                DateUtils.getMonthStartTime(month, year) to DateUtils.getMonthEndTime(month, year)
            }
            TimeRange.LAST_3_MONTHS -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.add(Calendar.MONTH, -2)
                val start = cal.timeInMillis
                cal.add(Calendar.MONTH, 3)
                cal.add(Calendar.DAY_OF_MONTH, -1)
                val end = cal.timeInMillis
                start to end
            }
            TimeRange.LAST_6_MONTHS -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.add(Calendar.MONTH, -5)
                val start = cal.timeInMillis
                cal.add(Calendar.MONTH, 6)
                cal.add(Calendar.DAY_OF_MONTH, -1)
                val end = cal.timeInMillis
                start to end
            }
            TimeRange.THIS_YEAR -> {
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.timeInMillis
                cal.set(Calendar.MONTH, Calendar.DECEMBER)
                cal.set(Calendar.DAY_OF_MONTH, 31)
                val end = cal.timeInMillis
                start to end
            }
        }
    }

    // 计算时间范围
    private val dateRange = _selectedTimeRange.flatMapLatest { timeRange ->
        val (start, end) = calcDateRange(timeRange)
        combine(_selectedMonth, _selectedYear) { _, _ -> start to end }
    }

    // 使用 flatMapLatest 动态切换 Flow，当时间范围变化时自动重新订阅
    val uiState: StateFlow<StatisticsUiState> = combine(dateRange, _selectedTimeRange) { range, timeRange ->
        range to timeRange
    }.flatMapLatest { (dateRangePair, timeRange) ->
        val (startTime, endTime) = dateRangePair
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
                selectedTimeRange = timeRange,
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

    val selectedTimeRange: TimeRange get() = _selectedTimeRange.value

    fun setTimeRange(range: TimeRange) {
        _selectedTimeRange.value = range
    }

    fun setDate(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
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