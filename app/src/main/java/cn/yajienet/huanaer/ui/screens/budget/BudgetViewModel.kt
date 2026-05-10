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
import cn.yajienet.huanaer.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
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
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val lastTriggerCount: Int = 0
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _showAddDialog = MutableStateFlow(false)
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    private val _budgetAmount = MutableStateFlow("")
    private val _lastTriggerCount = MutableStateFlow(0)
    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))

    // 使用 flatMapLatest 动态订阅预算数据
    private val budgetsFlow = combine(_currentMonth, _currentYear) { month, year ->
        month to year
    }.flatMapLatest { (month, year) ->
        budgetRepository.getByMonthYear(month, year)
    }

    // 分离数据订阅和 UI 状态
    private val dataFlow = combine(
        budgetsFlow,
        categoryRepository.getByType(TransactionType.EXPENSE)
    ) { budgets, categories ->
        DataState(budgets, categories)
    }

    private val dialogFlow = combine(
        _showAddDialog,
        _selectedCategoryId,
        _budgetAmount
    ) { showDialog, selectedId, amount ->
        DialogState(showDialog, selectedId, amount)
    }

    val uiState: StateFlow<BudgetUiState> = combine(
        dataFlow,
        dialogFlow,
        _lastTriggerCount,
        _currentMonth,
        _currentYear
    ) { data, dialog, triggerCount, month, year ->
        BudgetUiState(
            budgets = data.budgets,
            categories = data.categories,
            isLoading = false,
            showAddDialog = dialog.showDialog,
            selectedCategoryId = dialog.selectedId ?: data.categories.firstOrNull()?.id,
            budgetAmount = dialog.amount,
            currentMonth = month,
            currentYear = year,
            lastTriggerCount = triggerCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetUiState(isLoading = true))

    private data class DataState(val budgets: List<Budget>, val categories: List<Category>)
    private data class DialogState(val showDialog: Boolean, val selectedId: Long?, val amount: String)

    fun handleAddTrigger(trigger: Int) {
        if (trigger > _lastTriggerCount.value) {
            _lastTriggerCount.value = trigger
            showAddDialog()
        }
    }

    fun showAddDialog() {
        _showAddDialog.value = true
        _selectedCategoryId.value = uiState.value.categories.firstOrNull()?.id
        _budgetAmount.value = ""
    }

    fun hideAddDialog() {
        _showAddDialog.value = false
    }

    fun setCategory(categoryId: Long) {
        _selectedCategoryId.value = categoryId
    }

    fun setAmount(amount: String) {
        _budgetAmount.value = amount
    }

    fun addBudget() {
        val state = uiState.value
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