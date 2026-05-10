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

    // Tab 状态
    val selectedTab: Int = 0,  // 0=支出, 1=收入

    // 对话框状态
    val showAddDialog: Boolean = false,
    val dialogType: TransactionType = TransactionType.EXPENSE,
    val dialogName: String = "",
    val dialogColor: String = "#FF6B6B",

    // 编辑状态
    val showEditDialog: Boolean = false,
    val editingCategory: Category? = null
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

    // Tab 切换
    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    // 添加分类
    fun showAddDialog(type: TransactionType) {
        _uiState.update { it.copy(
            showAddDialog = true,
            dialogType = type,
            dialogName = "",
            dialogColor = if (type == TransactionType.EXPENSE) "#FF6B6B" else "#4CAF50",
            editingCategory = null
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
            val categories = if (state.dialogType == TransactionType.EXPENSE)
                state.expenseCategories else state.incomeCategories

            val category = CategoryEntity(
                name = state.dialogName,
                icon = "label",
                color = state.dialogColor,
                type = state.dialogType,
                sortOrder = categories.size,
                isDefault = false
            )
            categoryRepository.insert(category)
            hideAddDialog()
        }
    }

    // 编辑分类
    fun showEditDialog(category: Category) {
        _uiState.update { it.copy(
            showEditDialog = true,
            editingCategory = category,
            dialogName = category.name,
            dialogColor = category.color,
            dialogType = category.type
        ) }
    }

    fun hideEditDialog() {
        _uiState.update { it.copy(showEditDialog = false, editingCategory = null) }
    }

    fun updateCategory() {
        val state = _uiState.value
        val editing = state.editingCategory
        if (editing == null || state.dialogName.isBlank()) return

        viewModelScope.launch {
            val updated = CategoryEntity(
                id = editing.id,
                name = state.dialogName,
                icon = editing.icon,
                color = state.dialogColor,
                type = editing.type,
                sortOrder = editing.sortOrder,
                isDefault = editing.isDefault
            )
            categoryRepository.update(updated)
            hideEditDialog()
        }
    }

    // 删除分类
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

    // 拖拽排序
    fun moveCategory(fromIndex: Int, toIndex: Int) {
        val state = _uiState.value
        val categories = if (state.selectedTab == 0)
            state.expenseCategories.toMutableList()
        else
            state.incomeCategories.toMutableList()

        if (fromIndex < 0 || fromIndex >= categories.size ||
            toIndex < 0 || toIndex >= categories.size) return

        val item = categories.removeAt(fromIndex)
        categories.add(toIndex, item)

        // 更新 UI 状态
        if (state.selectedTab == 0) {
            _uiState.update { it.copy(expenseCategories = categories) }
        } else {
            _uiState.update { it.copy(incomeCategories = categories) }
        }

        // 保存排序到数据库
        viewModelScope.launch {
            categoryRepository.updateAllSortOrders(categories)
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