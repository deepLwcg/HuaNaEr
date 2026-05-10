package cn.yajienet.huanaer.ui.screens.category

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.data.local.entity.CategoryEntity
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryUiState(
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val editingCategory: Category? = null,
    val showAddDialog: Boolean = false,
    val dialogType: TransactionType = TransactionType.EXPENSE,
    val dialogName: String = "",
    val dialogColor: String = "#FF6B6B"
)

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getByType(TransactionType.EXPENSE).collect { categories ->
                _uiState.update { it.copy(expenseCategories = categories, isLoading = false) }
            }
        }
        viewModelScope.launch {
            categoryRepository.getByType(TransactionType.INCOME).collect { categories ->
                _uiState.update { it.copy(incomeCategories = categories) }
            }
        }
    }

    fun showAddDialog(type: TransactionType) {
        _uiState.update { it.copy(
            showAddDialog = true,
            dialogType = type,
            dialogName = "",
            dialogColor = if (type == TransactionType.EXPENSE) "#FF6B6B" else "#4CAF50"
        ) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false, editingCategory = null) }
    }

    fun setDialogName(name: String) {
        _uiState.update { it.copy(dialogName = name) }
    }

    fun setDialogColor(color: String) {
        _uiState.update { it.copy(dialogColor = color) }
    }

    fun addCategory() {
        val state = _uiState.value
        if (state.dialogName.isBlank()) return

        viewModelScope.launch {
            val category = CategoryEntity(
                name = state.dialogName,
                icon = "label",
                color = state.dialogColor,
                type = state.dialogType,
                sortOrder = if (state.dialogType == TransactionType.EXPENSE)
                    state.expenseCategories.size else state.incomeCategories.size
            )
            categoryRepository.insert(category)
            hideAddDialog()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(
                CategoryEntity(
                    id = category.id,
                    name = category.name,
                    icon = category.icon,
                    color = category.color,
                    type = category.type,
                    sortOrder = category.sortOrder,
                    isDefault = category.isDefault
                )
            )
        }
    }
}

class CategoryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as cn.yajienet.huanaer.HuaNaErApplication
            return CategoryViewModel(app.categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}