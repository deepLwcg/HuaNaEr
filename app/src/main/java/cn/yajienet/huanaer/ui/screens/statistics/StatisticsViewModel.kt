package cn.yajienet.huanaer.ui.screens.statistics

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.CategoryRepository
import cn.yajienet.huanaer.data.repository.TransactionRepository
import cn.yajienet.huanaer.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val startTime = DateUtils.getMonthStartTime(state.selectedMonth, state.selectedYear)
            val endTime = DateUtils.getMonthEndTime(state.selectedMonth, state.selectedYear)

            val totalExpense = transactionRepository.getTotalByTypeAndDateRange(
                TransactionType.EXPENSE, startTime, endTime
            ).first()

            val totalIncome = transactionRepository.getTotalByTypeAndDateRange(
                TransactionType.INCOME, startTime, endTime
            ).first()

            val expenseTotals = transactionRepository.getCategoryTotals(
                TransactionType.EXPENSE, startTime, endTime
            ).first()

            val incomeTotals = transactionRepository.getCategoryTotals(
                TransactionType.INCOME, startTime, endTime
            ).first()

            val categories = categoryRepository.getAll().first()

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

            _uiState.update {
                it.copy(
                    totalExpense = totalExpense,
                    totalIncome = totalIncome,
                    expenseByCategory = expenseStats,
                    incomeByCategory = incomeStats,
                    isLoading = false
                )
            }
        }
    }

    fun previousMonth() {
        val state = _uiState.value
        val newMonth = if (state.selectedMonth == 1) 12 else state.selectedMonth - 1
        val newYear = if (state.selectedMonth == 1) state.selectedYear - 1 else state.selectedYear
        _uiState.update { it.copy(selectedMonth = newMonth, selectedYear = newYear) }
        loadData()
    }

    fun nextMonth() {
        val state = _uiState.value
        val newMonth = if (state.selectedMonth == 12) 1 else state.selectedMonth + 1
        val newYear = if (state.selectedMonth == 12) state.selectedYear + 1 else state.selectedYear
        _uiState.update { it.copy(selectedMonth = newMonth, selectedYear = newYear) }
        loadData()
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