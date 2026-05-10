package cn.yajienet.huanaer.ui.screens.budget

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.local.entity.BudgetEntity
import cn.yajienet.huanaer.data.model.Budget
import cn.yajienet.huanaer.data.repository.BudgetRepository
import cn.yajienet.huanaer.data.repository.CategoryRepository
import cn.yajienet.huanaer.data.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class BudgetUiState(
    val budgets: List<Budget> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val selectedCategoryId: Long? = null,
    val budgetAmount: String = "",
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val budgets = budgetRepository.getByMonthYear(state.currentMonth, state.currentYear).first()
            val categories = categoryRepository.getByType(cn.yajienet.huanaer.data.model.TransactionType.EXPENSE).first()

            _uiState.update {
                it.copy(
                    budgets = budgets,
                    categories = categories,
                    isLoading = false
                )
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(
            showAddDialog = true,
            selectedCategoryId = it.categories.firstOrNull()?.id,
            budgetAmount = ""
        ) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun setCategory(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setAmount(amount: String) {
        _uiState.update { it.copy(budgetAmount = amount) }
    }

    fun addBudget() {
        val state = _uiState.value
        val categoryId = state.selectedCategoryId
        val amount = state.budgetAmount.toDoubleOrNull()

        if (categoryId == null || amount == null || amount <= 0) return

        viewModelScope.launch {
            val budget = BudgetEntity(
                categoryId = categoryId,
                amount = amount,
                month = state.currentMonth,
                year = state.currentYear
            )
            budgetRepository.insert(budget)
            hideAddDialog()
            loadData()
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.delete(
                BudgetEntity(
                    id = budget.id,
                    categoryId = budget.categoryId,
                    amount = budget.amount,
                    month = budget.month,
                    year = budget.year
                )
            )
            loadData()
        }
    }
}

class BudgetViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as cn.yajienet.huanaer.HuaNaErApplication
            return BudgetViewModel(app.budgetRepository, app.categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}