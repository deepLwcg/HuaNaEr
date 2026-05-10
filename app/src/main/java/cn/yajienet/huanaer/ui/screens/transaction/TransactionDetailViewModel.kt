package cn.yajienet.huanaer.ui.screens.transaction

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.local.entity.TransactionEntity
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.Transaction
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.CategoryRepository
import cn.yajienet.huanaer.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionDetailUiState(
    val transaction: Transaction? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedCategoryId: Long? = null,
    val categories: List<Category> = emptyList(),
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null
)

class TransactionDetailViewModel(
    private val transactionId: Long,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    init {
        loadTransaction()
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val transaction = transactionRepository.getById(transactionId)
            if (transaction != null) {
                loadCategories(transaction.type)
                _uiState.update { state ->
                    state.copy(
                        transaction = transaction,
                        isLoading = false,
                        amount = transaction.amount.toString(),
                        type = transaction.type,
                        selectedCategoryId = transaction.categoryId,
                        date = transaction.date,
                        note = transaction.note ?: ""
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "交易不存在") }
            }
        }
    }

    private fun loadCategories(type: TransactionType) {
        viewModelScope.launch {
            categoryRepository.getByType(type).collect { categories ->
                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        type = type
                    )
                }
            }
        }
    }

    fun startEditing() {
        _uiState.update { it.copy(isEditing = true) }
    }

    fun cancelEditing() {
        val transaction = _uiState.value.transaction
        if (transaction != null) {
            _uiState.update { state ->
                state.copy(
                    isEditing = false,
                    amount = transaction.amount.toString(),
                    type = transaction.type,
                    selectedCategoryId = transaction.categoryId,
                    date = transaction.date,
                    note = transaction.note ?: "",
                    error = null
                )
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
        val transaction = state.transaction

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "请输入有效金额") }
            return
        }

        if (categoryId == null) {
            _uiState.update { it.copy(error = "请选择分类") }
            return
        }

        if (transaction == null) {
            _uiState.update { it.copy(error = "交易不存在") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val updatedTransaction = TransactionEntity(
                    id = transaction.id,
                    amount = amount,
                    type = state.type,
                    categoryId = categoryId,
                    date = state.date,
                    note = state.note.ifBlank { null },
                    createdAt = transaction.createdAt,
                    updatedAt = System.currentTimeMillis()
                )
                transactionRepository.update(updatedTransaction)
                _uiState.update { it.copy(isSaving = false, saved = true, isEditing = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = "保存失败: ${e.message}") }
            }
        }
    }

    fun deleteTransaction() {
        val transaction = _uiState.value.transaction
        if (transaction == null) return

        viewModelScope.launch {
            try {
                transactionRepository.delete(
                    TransactionEntity(
                        id = transaction.id,
                        amount = transaction.amount,
                        type = transaction.type,
                        categoryId = transaction.categoryId,
                        date = transaction.date,
                        note = transaction.note,
                        createdAt = transaction.createdAt,
                        updatedAt = transaction.updatedAt
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

class TransactionDetailViewModelFactory(
    private val application: Application,
    private val transactionId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as cn.yajienet.huanaer.HuaNaErApplication
            return TransactionDetailViewModel(
                transactionId,
                app.transactionRepository,
                app.categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}