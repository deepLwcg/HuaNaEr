package cn.yajienet.huanaer.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.datastore.ThemeMode
import cn.yajienet.huanaer.data.datastore.ThemeStyle
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.components.CategoryCircleIcon
import cn.yajienet.huanaer.ui.components.DeleteConfirmationDialog
import cn.yajienet.huanaer.ui.components.neubru.BouncyIconButton
import cn.yajienet.huanaer.ui.components.neubru.NeubruCard
import cn.yajienet.huanaer.ui.components.neubru.NeubruSwitch
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateToCategoryManage: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(application))
    val uiState by viewModel.uiState.collectAsState()
    val colors = extendedColorScheme()

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
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // === 主题风格 ===
        item {
            NeubruCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BouncyIconButton(onClick = {}, size = 36.dp) {
                            Icon(Icons.Filled.Palette, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("主题风格", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ThemePreviewCard(
                            themeStyle = ThemeStyle.MINT_BREEZE,
                            label = "薄荷清风",
                            primaryColor = Color(0xFF00C896),
                            selected = uiState.themeStyle == ThemeStyle.MINT_BREEZE,
                            onClick = { viewModel.setThemeStyle(ThemeStyle.MINT_BREEZE) }
                        )
                        ThemePreviewCard(
                            themeStyle = ThemeStyle.SUNSET_GLOW,
                            label = "落日余晖",
                            primaryColor = Color(0xFFFF6B6B),
                            selected = uiState.themeStyle == ThemeStyle.SUNSET_GLOW,
                            onClick = { viewModel.setThemeStyle(ThemeStyle.SUNSET_GLOW) }
                        )
                        ThemePreviewCard(
                            themeStyle = ThemeStyle.MIDNIGHT_NEON,
                            label = "午夜霓虹",
                            primaryColor = Color(0xFF7B2FFF),
                            selected = uiState.themeStyle == ThemeStyle.MIDNIGHT_NEON,
                            onClick = { viewModel.setThemeStyle(ThemeStyle.MIDNIGHT_NEON) }
                        )
                    }
                }
            }
        }

        // === 外观设置 ===
        item {
            NeubruCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BouncyIconButton(onClick = {}, size = 36.dp) {
                            Icon(Icons.Filled.BrightnessMedium, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("外观", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(Modifier.height(12.dp))

                    // 主题模式选择
                    ThemeMode.entries.forEach { mode ->
                        val label = when (mode) {
                            ThemeMode.LIGHT -> "浅色"
                            ThemeMode.DARK -> "深色"
                            ThemeMode.SYSTEM -> "跟随系统"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(selected = uiState.themeMode == mode, onClick = { viewModel.setThemeMode(mode) })
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = uiState.themeMode == mode, onClick = { viewModel.setThemeMode(mode) })
                            Spacer(Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // 动态取色
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ColorLens, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("动态取色", style = MaterialTheme.typography.bodyLarge)
                                Text("根据壁纸调整颜色（Android 12+）", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        NeubruSwitch(
                            checked = uiState.dynamicColorEnabled,
                            onCheckedChange = { viewModel.setDynamicColorEnabled(it) }
                        )
                    }
                }
            }
        }

        // === 记账设置 ===
        item {
            NeubruCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BouncyIconButton(onClick = {}, size = 36.dp) {
                            Icon(Icons.Filled.Settings, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("记账", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(Modifier.height(12.dp))

                    // 分类管理
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateToCategoryManage() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Category, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("分类管理", style = MaterialTheme.typography.bodyLarge)
                            Text("添加、编辑、删除交易分类", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.height(8.dp))

                    // 月起始日
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showMonthStartDayDialog() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("每月起始日", style = MaterialTheme.typography.bodyLarge)
                            Text("每月 ${uiState.monthStartDay} 日", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.height(8.dp))

                    // 大额提醒阈值
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showLargeAmountDialog() }
                            .padding(vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("大额提醒阈值", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                if (uiState.largeAmountThreshold > 0) "¥${uiState.largeAmountThreshold.toInt()}" else "未启用",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.height(8.dp))

                    // 默认支出分类
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showDefaultCategoryDialog(TransactionType.EXPENSE) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("默认支出分类", style = MaterialTheme.typography.bodyLarge)
                            val name = uiState.expenseCategories.find { it.id == uiState.defaultExpenseCategoryId }?.name ?: "未设置"
                            Text(name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.height(8.dp))

                    // 默认收入分类
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showDefaultCategoryDialog(TransactionType.INCOME) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("默认收入分类", style = MaterialTheme.typography.bodyLarge)
                            val name = uiState.incomeCategories.find { it.id == uiState.defaultIncomeCategoryId }?.name ?: "未设置"
                            Text(name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // === 数据管理 ===
        item {
            NeubruCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BouncyIconButton(onClick = {}, size = 36.dp) {
                            Icon(Icons.Filled.Storage, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("数据备份", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.createBackup() },
                            enabled = !uiState.isBackingUp && !uiState.isImporting,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (uiState.isBackingUp) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                                Spacer(Modifier.width(8.dp))
                            }
                            Text("备份")
                        }

                        OutlinedButton(
                            onClick = { importLauncher.launch(arrayOf("application/zip")) },
                            enabled = !uiState.isBackingUp && !uiState.isImporting,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (uiState.isImporting) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                            }
                            Text("导入")
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "备份文件保存在应用专属存储目录",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // === 备份文件列表 ===
        if (uiState.backupFiles.isNotEmpty()) {
            item {
                Text(
                    "备份文件",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(uiState.backupFiles) { file ->
                NeubruCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 12.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Archive, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(file.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA) }
                            val fileSize = remember(file) { formatFileSize(file.length()) }
                            Text(
                                "${dateFormat.format(file.lastModified())} · $fileSize",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        BouncyIconButton(onClick = { viewModel.shareBackupFile(file) }, size = 32.dp) {
                            Icon(Icons.Filled.Share, "分享", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                        BouncyIconButton(onClick = { viewModel.showDeleteFileDialog(file, "backup") }, size = 32.dp) {
                            Icon(Icons.Filled.Delete, "删除", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                        TextButton(onClick = { viewModel.showRestoreDialog(file) }, enabled = !uiState.isRestoring) {
                            Text("恢复")
                        }
                    }
                }
            }
        }

        // === 危险操作 ===
        item {
            Spacer(Modifier.height(16.dp))
            NeubruCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BouncyIconButton(onClick = {}, size = 36.dp) {
                        Icon(Icons.Filled.DeleteForever, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("清除所有数据", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.error)
                        Text("删除所有交易记录、分类和预算", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                    Button(
                        onClick = { viewModel.showClearDataDialog() },
                        enabled = !uiState.isClearing,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        if (uiState.isClearing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onError)
                        } else {
                            Text("清除")
                        }
                    }
                }
            }
        }

        // === 关于 ===
        item {
            Spacer(Modifier.height(16.dp))
            NeubruCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BouncyIconButton(onClick = {}, size = 64.dp, hapticEnabled = false) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Palette, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("花哪儿了", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "版本 ${getAppVersion(context)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "简洁高效的个人记账工具",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "轻松记录每一笔支出与收入",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "© YajieNet",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    // 对话框
    if (uiState.showMonthStartDayDialog) {
        MonthStartDayDialog(
            currentDay = uiState.monthStartDay,
            onDismiss = { viewModel.hideMonthStartDayDialog() },
            onConfirm = { viewModel.setMonthStartDay(it) }
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
                Button(onClick = { viewModel.restoreFromBackup() }) { Text("恢复") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideRestoreDialog() }) { Text("取消") }
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
                ) { Text("删除") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteFileDialog() }) { Text("取消") }
            }
        )
    }

    if (uiState.showRestartDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideRestartDialog() },
            icon = { Icon(Icons.Filled.Restore, "恢复") },
            title = { Text("恢复成功") },
            text = { Text("数据已恢复成功，需要重启应用才能生效。") },
            confirmButton = {
                Button(onClick = { viewModel.restartApp() }) { Text("立即重启") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideRestartDialog() }) { Text("稍后重启") }
            }
        )
    }

    if (uiState.showDefaultCategoryDialog && uiState.defaultCategoryType != null) {
        val categories = if (uiState.defaultCategoryType == TransactionType.EXPENSE)
            uiState.expenseCategories else uiState.incomeCategories
        val currentId = if (uiState.defaultCategoryType == TransactionType.EXPENSE)
            uiState.defaultExpenseCategoryId else uiState.defaultIncomeCategoryId
        val title = if (uiState.defaultCategoryType == TransactionType.EXPENSE)
            "默认支出分类" else "默认收入分类"

        AlertDialog(
            onDismissRequest = { viewModel.hideDefaultCategoryDialog() },
            title = { Text(title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = category.id == currentId,
                                    onClick = { viewModel.setDefaultCategory(category.id) }
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = category.id == currentId,
                                onClick = { viewModel.setDefaultCategory(category.id) }
                            )
                            Spacer(Modifier.width(8.dp))
                            CategoryCircleIcon(
                                name = category.name,
                                color = category.color,
                                size = 24.dp,
                                textStyle = MaterialTheme.typography.labelSmall
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(category.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    if (currentId > 0) {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setDefaultCategory(-1L) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentId <= 0,
                                onClick = { viewModel.setDefaultCategory(-1L) }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("不设置默认", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.hideDefaultCategoryDialog() }) { Text("关闭") }
            }
        )
    }
}

@Composable
private fun ThemePreviewCard(
    themeStyle: ThemeStyle,
    label: String,
    primaryColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "themeScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Box(modifier = Modifier.size(72.dp).scale(scale)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.1f),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height * 0.15f),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.3f),
                    topLeft = Offset(8.dp.toPx(), size.height * 0.2f),
                    size = Size(size.width - 16.dp.toPx(), size.height * 0.35f),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.08f),
                    topLeft = Offset(0f, size.height * 0.82f),
                    size = Size(size.width, size.height * 0.18f),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                drawRoundRect(
                    color = if (selected) primaryColor else Color.LightGray,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    style = Stroke(width = if (selected) 3.dp.toPx() else 1.dp.toPx())
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (selected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
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

private fun getAppVersion(context: android.content.Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }
}