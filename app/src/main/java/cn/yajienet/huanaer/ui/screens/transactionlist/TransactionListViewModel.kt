package cn.yajienet.huanaer.ui.screens.transactionlist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.model.Transaction
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.TransactionRepository
import cn.yajienet.huanaer.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

enum class TimeSelectionMode {
    MONTH,
    CUSTOM
}

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val selectedMonth: Int = DateUtils.getCurrentMonth(),
    val selectedYear: Int = DateUtils.getCurrentYear(),
    val startDate: Long = DateUtils.getMonthStartTime(DateUtils.getCurrentMonth(), DateUtils.getCurrentYear()),
    val endDate: Long = DateUtils.getMonthEndTime(DateUtils.getCurrentMonth(), DateUtils.getCurrentYear()),
    val selectionMode: TimeSelectionMode = TimeSelectionMode.MONTH,
    val isLoading: Boolean = true
)

class TransactionListViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(DateUtils.getCurrentMonth())
    private val _selectedYear = MutableStateFlow(DateUtils.getCurrentYear())
    private val _startDate = MutableStateFlow(
        DateUtils.getMonthStartTime(DateUtils.getCurrentMonth(), DateUtils.getCurrentYear())
    )
    private val _endDate = MutableStateFlow(
        DateUtils.getMonthEndTime(DateUtils.getCurrentMonth(), DateUtils.getCurrentYear())
    )
    private val _selectionMode = MutableStateFlow(TimeSelectionMode.MONTH)

    private val dateRange = combine(
        _selectedMonth, _selectedYear, _startDate, _endDate, _selectionMode
    ) { month, year, start, end, mode ->
        if (mode == TimeSelectionMode.MONTH) {
            DateUtils.getMonthStartTime(month, year) to DateUtils.getMonthEndTime(month, year)
        } else {
            start to end
        }
    }

    private val selectionState = combine(
        _selectedMonth, _selectedYear, _startDate, _endDate, _selectionMode
    ) { month, year, start, end, mode ->
        SelectionState(month, year, start, end, mode)
    }

    private data class SelectionState(
        val month: Int,
        val year: Int,
        val start: Long,
        val end: Long,
        val mode: TimeSelectionMode
    )

    val uiState: StateFlow<TransactionListUiState> = dateRange.flatMapLatest { (startTime, endTime) ->
        combine(
            transactionRepository.getByDateRange(startTime, endTime),
            transactionRepository.getTotalByTypeAndDateRange(TransactionType.INCOME, startTime, endTime),
            transactionRepository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, startTime, endTime),
            selectionState
        ) { transactions, income, expense, state ->
            TransactionListUiState(
                transactions = transactions.sortedByDescending { it.date },
                totalIncome = income,
                totalExpense = expense,
                balance = income - expense,
                selectedMonth = state.month,
                selectedYear = state.year,
                startDate = state.start,
                endDate = state.end,
                selectionMode = state.mode,
                isLoading = false
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionListUiState(isLoading = true))

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

    fun setDate(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
    }

    fun setSelectionMode(mode: TimeSelectionMode) {
        _selectionMode.value = mode
        if (mode == TimeSelectionMode.MONTH) {
            _startDate.value = DateUtils.getMonthStartTime(_selectedMonth.value, _selectedYear.value)
            _endDate.value = DateUtils.getMonthEndTime(_selectedMonth.value, _selectedYear.value)
        } else {
            _startDate.value = DateUtils.getMonthStartTime(_selectedMonth.value, _selectedYear.value)
            _endDate.value = DateUtils.getMonthEndTime(_selectedMonth.value, _selectedYear.value)
        }
    }

    fun setCustomDateRange(start: Long, end: Long) {
        _startDate.value = start
        _endDate.value = end
    }
}

class TransactionListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as cn.yajienet.huanaer.HuaNaErApplication
            return TransactionListViewModel(app.transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}