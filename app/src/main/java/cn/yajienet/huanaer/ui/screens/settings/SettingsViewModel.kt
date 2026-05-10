package cn.yajienet.huanaer.ui.screens.settings

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.yajienet.huanaer.HuaNaErApplication
import cn.yajienet.huanaer.data.datastore.ThemeMode
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.data.repository.CategoryRepository
import cn.yajienet.huanaer.data.repository.SettingsRepository
import cn.yajienet.huanaer.data.repository.TransactionRepository
import cn.yajienet.huanaer.util.BackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true,

    // 记账设置
    val monthStartDay: Int = 1,
    val defaultExpenseCategoryId: Long = -1L,
    val defaultIncomeCategoryId: Long = -1L,
    val largeAmountThreshold: Double = 0.0,
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val showMonthStartDayDialog: Boolean = false,
    val showDefaultCategoryDialog: Boolean = false,
    val defaultCategoryType: TransactionType? = null,
    val showLargeAmountDialog: Boolean = false,

    // 数据管理
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val isClearing: Boolean = false,
    val isImporting: Boolean = false,

    val message: String? = null,
    val errorMessage: String? = null,

    val backupFiles: List<File> = emptyList(),

    val showRestoreDialog: Boolean = false,
    val selectedBackupFile: File? = null,
    val showClearDataDialog: Boolean = false,
    val showDeleteFileDialog: Boolean = false,
    val fileToDelete: File? = null,
    val deleteFileType: String? = null, // "backup" or "export"
    val showRestartDialog: Boolean = false,

    // 导航
    val navigateToCategoryManage: Boolean = false
)

class SettingsViewModel(
    private val application: Application,
    private val settingsRepository: SettingsRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val backupManager = BackupManager(application)

    init {
        loadSettings()
        loadCategories()
        loadBackupFiles()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            settingsRepository.dynamicColor.collect { enabled ->
                _uiState.update { it.copy(dynamicColorEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            settingsRepository.monthStartDay.collect { day ->
                _uiState.update { it.copy(monthStartDay = day) }
            }
        }
        viewModelScope.launch {
            settingsRepository.defaultExpenseCategoryId.collect { id ->
                _uiState.update { it.copy(defaultExpenseCategoryId = id) }
            }
        }
        viewModelScope.launch {
            settingsRepository.defaultIncomeCategoryId.collect { id ->
                _uiState.update { it.copy(defaultIncomeCategoryId = id) }
            }
        }
        viewModelScope.launch {
            settingsRepository.largeAmountThreshold.collect { threshold ->
                _uiState.update { it.copy(largeAmountThreshold = threshold) }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getByType(TransactionType.EXPENSE).collect { categories ->
                _uiState.update { it.copy(expenseCategories = categories) }
            }
        }
        viewModelScope.launch {
            categoryRepository.getByType(TransactionType.INCOME).collect { categories ->
                _uiState.update { it.copy(incomeCategories = categories) }
            }
        }
    }

    private fun loadBackupFiles() {
        viewModelScope.launch {
            val files = backupManager.getBackupFiles()
            _uiState.update { it.copy(backupFiles = files) }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColor(enabled)
        }
    }

    // 记账设置
    fun showMonthStartDayDialog() {
        _uiState.update { it.copy(showMonthStartDayDialog = true) }
    }

    fun hideMonthStartDayDialog() {
        _uiState.update { it.copy(showMonthStartDayDialog = false) }
    }

    fun setMonthStartDay(day: Int) {
        viewModelScope.launch {
            settingsRepository.setMonthStartDay(day)
        }
        hideMonthStartDayDialog()
    }

    fun showDefaultCategoryDialog(type: TransactionType) {
        _uiState.update { it.copy(showDefaultCategoryDialog = true, defaultCategoryType = type) }
    }

    fun hideDefaultCategoryDialog() {
        _uiState.update { it.copy(showDefaultCategoryDialog = false, defaultCategoryType = null) }
    }

    fun setDefaultCategory(categoryId: Long) {
        viewModelScope.launch {
            val type = _uiState.value.defaultCategoryType
            if (type == TransactionType.EXPENSE) {
                settingsRepository.setDefaultExpenseCategoryId(categoryId)
            } else if (type == TransactionType.INCOME) {
                settingsRepository.setDefaultIncomeCategoryId(categoryId)
            }
        }
        hideDefaultCategoryDialog()
    }

    fun showLargeAmountDialog() {
        _uiState.update { it.copy(showLargeAmountDialog = true) }
    }

    fun hideLargeAmountDialog() {
        _uiState.update { it.copy(showLargeAmountDialog = false) }
    }

    fun setLargeAmountThreshold(threshold: Double) {
        viewModelScope.launch {
            settingsRepository.setLargeAmountThreshold(threshold)
        }
        hideLargeAmountDialog()
    }

    fun navigateToCategoryManage() {
        _uiState.update { it.copy(navigateToCategoryManage = true) }
    }

    fun onNavigatedToCategoryManage() {
        _uiState.update { it.copy(navigateToCategoryManage = false) }
    }

    fun createBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBackingUp = true, message = null, errorMessage = null) }

            val result = backupManager.createBackup()

            _uiState.update {
                it.copy(
                    isBackingUp = false,
                    message = if (result.success) "备份成功" else null,
                    errorMessage = if (!result.success) result.error else null,
                    backupFiles = backupManager.getBackupFiles()
                )
            }
        }
    }

    fun showRestoreDialog(file: File) {
        _uiState.update { it.copy(showRestoreDialog = true, selectedBackupFile = file) }
    }

    fun hideRestoreDialog() {
        _uiState.update { it.copy(showRestoreDialog = false, selectedBackupFile = null) }
    }

    fun restoreFromBackup() {
        val file = _uiState.value.selectedBackupFile ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isRestoring = true, message = null, errorMessage = null) }

            // 先关闭数据库连接
            val app = application as HuaNaErApplication
            app.database.close()

            val result = backupManager.restoreFromBackup(file)

            _uiState.update {
                it.copy(
                    isRestoring = false,
                    showRestoreDialog = false,
                    selectedBackupFile = null,
                    showRestartDialog = result.success,
                    errorMessage = if (!result.success) result.error else null
                )
            }
        }
    }

    fun hideRestartDialog() {
        _uiState.update { it.copy(showRestartDialog = false) }
    }

    fun restartApp() {
        // 重启应用
        val intent = application.packageManager.getLaunchIntentForPackage(application.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
        // 结束当前进程
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun showClearDataDialog() {
        _uiState.update { it.copy(showClearDataDialog = true) }
    }

    fun hideClearDataDialog() {
        _uiState.update { it.copy(showClearDataDialog = false) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearing = true, message = null, errorMessage = null) }

            val app = application as HuaNaErApplication
            val success = backupManager.clearAllData(app.database)

            _uiState.update {
                it.copy(
                    isClearing = false,
                    showClearDataDialog = false,
                    message = if (success) "数据已清除" else null,
                    errorMessage = if (!success) "清除失败" else null
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, errorMessage = null) }
    }

    // 删除文件
    fun showDeleteFileDialog(file: File, type: String) {
        _uiState.update { it.copy(showDeleteFileDialog = true, fileToDelete = file, deleteFileType = type) }
    }

    fun hideDeleteFileDialog() {
        _uiState.update { it.copy(showDeleteFileDialog = false, fileToDelete = null, deleteFileType = null) }
    }

    fun deleteFile() {
        val file = _uiState.value.fileToDelete
        val type = _uiState.value.deleteFileType
        if (file == null || type == null) return

        viewModelScope.launch {
            val success = file.delete()
            _uiState.update {
                it.copy(
                    showDeleteFileDialog = false,
                    fileToDelete = null,
                    deleteFileType = null,
                    message = if (success) "文件已删除" else null,
                    errorMessage = if (!success) "删除失败" else null,
                    backupFiles = backupManager.getBackupFiles()
                )
            }
        }
    }

    // 分享备份文件
    fun shareBackupFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                application,
                "${application.packageName}.fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            application.startActivity(Intent.createChooser(shareIntent, "分享备份文件").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "分享失败: ${e.message}") }
        }
    }

    // 导入外部数据
    fun importFromExternalFile(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, message = null, errorMessage = null) }

            val result = backupManager.restoreFromBackup(file)

            _uiState.update {
                it.copy(
                    isImporting = false,
                    message = if (result.success) "导入成功，请重启应用" else null,
                    errorMessage = if (!result.success) result.error else null
                )
            }
        }
    }
}

class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as HuaNaErApplication
            return SettingsViewModel(
                application,
                app.settingsRepository,
                app.transactionRepository,
                app.categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}