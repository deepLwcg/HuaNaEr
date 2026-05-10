package cn.yajienet.huanaer.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.datastore.ThemeMode
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.components.DeleteConfirmationDialog
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateToCategoryManage: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(application))
    val uiState by viewModel.uiState.collectAsState()

    // 导入外部文件
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use { input ->
                    val tempFile = File(context.cacheDir, "import_temp.zip")
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                    viewModel.importFromExternalFile(tempFile)
                    tempFile.delete()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "导入失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 导航到分类管理
    LaunchedEffect(uiState.navigateToCategoryManage) {
        if (uiState.navigateToCategoryManage) {
            onNavigateToCategoryManage()
            viewModel.onNavigatedToCategoryManage()
        }
    }

    LaunchedEffect(uiState.message, uiState.errorMessage) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // === 显示设置 ===
        item {
            SettingsSectionHeader(title = "显示设置")
        }

        item {
            ThemeModeCard(
                currentMode = uiState.themeMode,
                onModeChange = { viewModel.setThemeMode(it) }
            )
        }

        item {
            DynamicColorCard(
                enabled = uiState.dynamicColorEnabled,
                onToggle = { viewModel.setDynamicColorEnabled(it) }
            )
        }

        // === 记账设置 ===
        item {
            Spacer(Modifier.height(16.dp))
            SettingsSectionHeader(title = "记账设置")
        }

        item {
            AccountingSettingsCard(
                monthStartDay = uiState.monthStartDay,
                defaultExpenseCategory = uiState.expenseCategories.find { it.id == uiState.defaultExpenseCategoryId },
                defaultIncomeCategory = uiState.incomeCategories.find { it.id == uiState.defaultIncomeCategoryId },
                largeAmountThreshold = uiState.largeAmountThreshold,
                onCategoryManageClick = { viewModel.navigateToCategoryManage() },
                onMonthStartDayClick = { viewModel.showMonthStartDayDialog() },
                onDefaultExpenseClick = { viewModel.showDefaultCategoryDialog(TransactionType.EXPENSE) },
                onDefaultIncomeClick = { viewModel.showDefaultCategoryDialog(TransactionType.INCOME) },
                onLargeAmountClick = { viewModel.showLargeAmountDialog() }
            )
        }

        // === 数据管理 ===
        item {
            Spacer(Modifier.height(16.dp))
            SettingsSectionHeader(title = "数据管理")
        }

        item {
            DataManagementCard(
                isBackingUp = uiState.isBackingUp,
                isImporting = uiState.isImporting,
                onBackup = { viewModel.createBackup() },
                onImport = { importLauncher.launch(arrayOf("application/zip")) }
            )
        }

        // === 备份文件列表 ===
        if (uiState.backupFiles.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                SettingsSectionHeader(title = "备份文件")
            }

            items(uiState.backupFiles) { file ->
                BackupFileItem(
                    file = file,
                    isRestoring = uiState.isRestoring,
                    onRestore = { viewModel.showRestoreDialog(file) },
                    onShare = { viewModel.shareBackupFile(file) },
                    onDelete = { viewModel.showDeleteFileDialog(file, "backup") }
                )
            }
        }

        // === 危险操作 ===
        item {
            Spacer(Modifier.height(16.dp))
            SettingsSectionHeader(title = "危险操作", isDanger = true)
        }

        item {
            ClearDataCard(
                isClearing = uiState.isClearing,
                onClear = { viewModel.showClearDataDialog() }
            )
        }

        // === 关于 ===
        item {
            Spacer(Modifier.height(24.dp))
            AboutCard()
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }

    // 各种对话框
    if (uiState.showMonthStartDayDialog) {
        MonthStartDayDialog(
            currentDay = uiState.monthStartDay,
            onDismiss = { viewModel.hideMonthStartDayDialog() },
            onConfirm = { viewModel.setMonthStartDay(it) }
        )
    }

    if (uiState.showDefaultCategoryDialog && uiState.defaultCategoryType != null) {
        val categories = if (uiState.defaultCategoryType == TransactionType.EXPENSE) {
            uiState.expenseCategories
        } else {
            uiState.incomeCategories
        }
        val currentId = if (uiState.defaultCategoryType == TransactionType.EXPENSE) {
            uiState.defaultExpenseCategoryId
        } else {
            uiState.defaultIncomeCategoryId
        }
        DefaultCategoryDialog(
            categories = categories,
            currentCategoryId = currentId,
            title = if (uiState.defaultCategoryType == TransactionType.EXPENSE) "默认支出分类" else "默认收入分类",
            onDismiss = { viewModel.hideDefaultCategoryDialog() },
            onConfirm = { viewModel.setDefaultCategory(it) }
        )
    }

    if (uiState.showLargeAmountDialog) {
        LargeAmountDialog(
            currentThreshold = uiState.largeAmountThreshold,
            onDismiss = { viewModel.hideLargeAmountDialog() },
            onConfirm = { viewModel.setLargeAmountThreshold(it) }
        )
    }

    if (uiState.showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideRestoreDialog() },
            icon = { Icon(Icons.Filled.Restore, "恢复") },
            title = { Text("确认恢复") },
            text = { Text("确定要从备份恢复数据吗？\n当前数据将被覆盖，恢复后需要重启应用。") },
            confirmButton = {
                Button(onClick = { viewModel.restoreFromBackup() }) {
                    Text("恢复")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideRestoreDialog() }) {
                    Text("取消")
                }
            }
        )
    }

    if (uiState.showClearDataDialog) {
        DeleteConfirmationDialog(
            onDismiss = { viewModel.hideClearDataDialog() },
            onConfirm = { viewModel.clearAllData() },
            title = "清除所有数据",
            message = "确定要清除所有数据吗？此操作不可恢复！建议先备份数据。"
        )
    }

    // 删除文件确认对话框
    if (uiState.showDeleteFileDialog && uiState.fileToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteFileDialog() },
            icon = { Icon(Icons.Filled.Delete, "删除") },
            title = { Text("删除文件") },
            text = { Text("确定要删除 ${uiState.fileToDelete!!.name} 吗？") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteFile() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteFileDialog() }) {
                    Text("取消")
                }
            }
        )
    }

    // 恢复成功重启对话框
    if (uiState.showRestartDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideRestartDialog() },
            icon = { Icon(Icons.Filled.Restore, "恢复") },
            title = { Text("恢复成功") },
            text = { Text("数据已恢复成功，需要重启应用才能生效。") },
            confirmButton = {
                Button(onClick = { viewModel.restartApp() }) {
                    Text("立即重启")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideRestartDialog() }) {
                    Text("稍后重启")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    isDanger: Boolean = false,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun ThemeModeCard(
    currentMode: ThemeMode,
    onModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(text = "主题模式", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(12.dp))

            ThemeMode.entries.forEach { mode ->
                val label = when (mode) {
                    ThemeMode.LIGHT -> "浅色模式"
                    ThemeMode.DARK -> "深色模式"
                    ThemeMode.SYSTEM -> "跟随系统"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(selected = currentMode == mode, onClick = { onModeChange(mode) })
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = currentMode == mode, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun DynamicColorCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.ColorLens, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "动态颜色", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "根据壁纸自动调整应用颜色（Android 12+）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = enabled, onCheckedChange = onToggle)
        }
    }
}

@Composable
private fun AccountingSettingsCard(
    monthStartDay: Int,
    defaultExpenseCategory: Category?,
    defaultIncomeCategory: Category?,
    largeAmountThreshold: Double,
    onCategoryManageClick: () -> Unit,
    onMonthStartDayClick: () -> Unit,
    onDefaultExpenseClick: () -> Unit,
    onDefaultIncomeClick: () -> Unit,
    onLargeAmountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(text = "记账设置", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))

            // 分类管理入口
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = false, onClick = onCategoryManageClick)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Category, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("分类管理", style = MaterialTheme.typography.bodyLarge)
                    Text("添加、编辑、删除交易分类", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Filled.ImportExport, contentDescription = "进入", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(8.dp))

            // 每月起始日
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = false, onClick = onMonthStartDayClick)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("每月起始日", style = MaterialTheme.typography.bodyLarge)
                    Text("每月 ${monthStartDay} 日", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(8.dp))

            // 默认支出分类
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = false, onClick = onDefaultExpenseClick)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("默认支出分类", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        defaultExpenseCategory?.name ?: "未设置",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (defaultExpenseCategory != null) {
                    CategoryCircleIcon(
                        name = defaultExpenseCategory.name,
                        color = defaultExpenseCategory.color,
                        size = 24.dp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 默认收入分类
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = false, onClick = onDefaultIncomeClick)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("默认收入分类", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        defaultIncomeCategory?.name ?: "未设置",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (defaultIncomeCategory != null) {
                    CategoryCircleIcon(
                        name = defaultIncomeCategory.name,
                        color = defaultIncomeCategory.color,
                        size = 24.dp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 大额提醒阈值
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = false, onClick = onLargeAmountClick)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = if (largeAmountThreshold > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("大额提醒阈值", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        if (largeAmountThreshold > 0) "¥${largeAmountThreshold}" else "未启用",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DataManagementCard(
    isBackingUp: Boolean,
    isImporting: Boolean,
    onBackup: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(text = "数据备份", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBackup,
                    enabled = !isBackingUp && !isImporting,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isBackingUp) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("备份")
                }

                OutlinedButton(
                    onClick = onImport,
                    enabled = !isBackingUp && !isImporting,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("导入")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "备份文件保存在应用专属存储目录",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BackupFileItem(
    file: File,
    isRestoring: Boolean,
    onRestore: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA) }
    val fileSize = remember(file) { formatFileSize(file.length()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Archive, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = file.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "${dateFormat.format(file.lastModified())} · $fileSize", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onShare, enabled = !isRestoring) {
                Icon(Icons.Filled.Share, contentDescription = "分享", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete, enabled = !isRestoring) {
                Icon(Icons.Filled.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
            }
            TextButton(onClick = onRestore, enabled = !isRestoring) {
                Text("恢复")
            }
        }
    }
}

@Composable
private fun ClearDataCard(
    isClearing: Boolean,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "清除所有数据", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                Text(text = "删除所有交易记录、分类和预算数据", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
            }
            Button(
                onClick = onClear,
                enabled = !isClearing,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                if (isClearing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onError)
                } else {
                    Text("清除")
                }
            }
        }
    }
}

@Composable
private fun AboutCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "花哪儿", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(text = "版本 1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(text = "简洁的个人记账应用", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MonthStartDayDialog(
    currentDay: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(currentDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("每月起始日") },
        text = {
            Column {
                Text("设置统计周期的起始日（1-28日）")
                Spacer(Modifier.height(16.dp))
                Text("当前: ${selectedDay} 日", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Slider(
                    value = selectedDay.toFloat(),
                    onValueChange = { selectedDay = it.toInt() },
                    valueRange = 1f..28f,
                    steps = 27
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedDay) }) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
private fun DefaultCategoryDialog(
    categories: List<Category>,
    currentCategoryId: Long,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var selectedId by remember { mutableStateOf(currentCategoryId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                if (categories.isEmpty()) {
                    Text("暂无分类，请先添加分类")
                } else {
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(selected = selectedId == category.id, onClick = { selectedId = category.id })
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedId == category.id, onClick = null)
                            Spacer(Modifier.width(8.dp))
                            CategoryCircleIcon(
                            name = category.name,
                            color = category.color,
                            size = 32.dp
                        )
                            Spacer(Modifier.width(8.dp))
                            Text(category.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedId) }, enabled = categories.isNotEmpty()) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
private fun LargeAmountDialog(
    currentThreshold: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var inputValue by remember { mutableStateOf(if (currentThreshold > 0) currentThreshold.toInt().toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("大额提醒阈值") },
        text = {
            Column {
                Text("当支出金额超过此值时提醒确认（0 或留空表示不提醒）")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it.filter { c -> c.isDigit() } },
                    label = { Text("金额（元）") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val value = inputValue.toDoubleOrNull() ?: 0.0
                onConfirm(value)
            }) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}