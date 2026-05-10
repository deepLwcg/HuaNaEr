package cn.yajienet.huanaer.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.repository.TransactionRepository
import cn.yajienet.huanaer.data.model.Transaction
import cn.yajienet.huanaer.data.model.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class HomeUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    private val monthStartTime: Long = getMonthStartTime(currentMonth, currentYear)
    private val monthEndTime: Long = getMonthEndTime(currentMonth, currentYear)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getTotalByTypeAndDateRange(TransactionType.INCOME, monthStartTime, monthEndTime),
        repository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, monthStartTime, monthEndTime),
        repository.getRecentTransactions(10)
    ) { income, expense, transactions ->
        HomeUiState(
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense,
            recentTransactions = transactions,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState(isLoading = true)
    )

    private fun getMonthStartTime(month: Int, year: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getMonthEndTime(month: Int, year: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.MONTH, 1)
        return cal.timeInMillis
    }
}

class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as cn.yajienet.huanaer.HuaNaErApplication
            return HomeViewModel(app.transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}