package cn.yajienet.huanaer.ui.screens.transaction

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.local.entity.TransactionEntity
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.CategoryRepository
import cn.yajienet.huanaer.data.repository.SettingsRepository
import cn.yajienet.huanaer.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedCategoryId: Long? = null,
    val categories: List<Category> = emptyList(),
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        loadCategories(TransactionType.EXPENSE)
    }

    fun loadCategories(type: TransactionType) {
        viewModelScope.launch {
            // 获取默认分类配置
            val defaultCategoryId = if (type == TransactionType.EXPENSE) {
                settingsRepository.defaultExpenseCategoryId.first()
            } else {
                settingsRepository.defaultIncomeCategoryId.first()
            }

            categoryRepository.getByType(type).collect { categories ->
                // 优先选择配置的默认分类，如果没有则选择第一个
                val selectedId = if (defaultCategoryId > 0 && categories.any { it.id == defaultCategoryId }) {
                    defaultCategoryId
                } else {
                    categories.firstOrNull()?.id
                }

                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        type = type,
                        selectedCategoryId = selectedId
                    )
                }
            }
        }
    }

    fun setAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun setType(type: TransactionType) {
        loadCategories(type)
    }

    fun setCategory(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setDate(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun saveTransaction() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()
        val categoryId = state.selectedCategoryId

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "请输入有效金额") }
            return
        }

        if (categoryId == null) {
            _uiState.update { it.copy(error = "请选择分类") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val transaction = TransactionEntity(
                    amount = amount,
                    type = state.type,
                    categoryId = categoryId,
                    date = state.date,
                    note = state.note.ifBlank { null }
                )
                transactionRepository.insert(transaction)
                _uiState.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = "保存失败: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

class TransactionViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as cn.yajienet.huanaer.HuaNaErApplication
            return TransactionViewModel(
                app.transactionRepository,
                app.categoryRepository,
                app.settingsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}