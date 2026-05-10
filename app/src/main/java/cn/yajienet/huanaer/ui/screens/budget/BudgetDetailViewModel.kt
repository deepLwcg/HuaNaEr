package cn.yajienet.huanaer.ui.screens.budget

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.local.entity.BudgetEntity
import cn.yajienet.huanaer.data.model.Budget
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.BudgetRepository
import cn.yajienet.huanaer.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BudgetDetailUiState(
    val budget: Budget? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val amount: String = "",
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null
)

class BudgetDetailViewModel(
    private val budgetId: Long,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetDetailUiState())
    val uiState: StateFlow<BudgetDetailUiState> = _uiState.asStateFlow()

    init {
        loadBudget()
    }

    private fun loadBudget() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val budget = budgetRepository.getById(budgetId)
            if (budget != null) {
                _uiState.update { state ->
                    state.copy(
                        budget = budget,
                        isLoading = false,
                        amount = budget.amount.toString()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "预算不存在") }
            }
        }
    }

    fun startEditing() {
        _uiState.update { it.copy(isEditing = true) }
    }

    fun cancelEditing() {
        val budget = _uiState.value.budget
        if (budget != null) {
            _uiState.update { state ->
                state.copy(
                    isEditing = false,
                    amount = budget.amount.toString(),
                    error = null
                )
            }
        }
    }

    fun setAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun saveBudget() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()
        val budget = state.budget

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "请输入有效金额") }
            return
        }

        if (budget == null) {
            _uiState.update { it.copy(error = "预算不存在") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            try {
                val updatedBudget = BudgetEntity(
                    id = budget.id,
                    categoryId = budget.categoryId,
                    amount = amount,
                    month = budget.month,
                    year = budget.year,
                    createdAt = budget.createdAt
                )
                budgetRepository.update(updatedBudget)
                _uiState.update { it.copy(saved = true, isEditing = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "保存失败: ${e.message}") }
            }
        }
    }

    fun deleteBudget() {
        val budget = _uiState.value.budget
        if (budget == null) return

        viewModelScope.launch {
            try {
                budgetRepository.delete(
                    BudgetEntity(
                        id = budget.id,
                        categoryId = budget.categoryId,
                        amount = budget.amount,
                        month = budget.month,
                        year = budget.year,
                        createdAt = budget.createdAt
                    )
                )
                _uiState.update { it.copy(deleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "删除失败: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

class BudgetDetailViewModelFactory(
    private val application: Application,
    private val budgetId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as cn.yajienet.huanaer.HuaNaErApplication
            return BudgetDetailViewModel(
                budgetId,
                app.budgetRepository,
                app.categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}