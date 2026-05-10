package cn.yajienet.huanaer.ui.screens.category

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.components.LoadingState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManageScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()

    // 拖拽状态
    var draggingIndex by remember { mutableIntStateOf(-1) }
    var dragOffsetY by remember { mutableStateOf(0f) }

    val categories = if (uiState.selectedTab == 0)
        uiState.expenseCategories else uiState.incomeCategories

    if (uiState.isLoading) {
        LoadingState()
    } else {
        var showAddMenu by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("分类管理") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showAddMenu = true }) {
                                Icon(Icons.Filled.Add, contentDescription = "添加分类")
                            }
                            DropdownMenu(
                                expanded = showAddMenu,
                                onDismissRequest = { showAddMenu = false },
                                modifier = Modifier.width(64.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .clickable {
                                            showAddMenu = false
                                            viewModel.showAddDialog(TransactionType.EXPENSE)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "支出",
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .clickable {
                                            showAddMenu = false
                                            viewModel.showAddDialog(TransactionType.INCOME)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "收入",
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tab 分页
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        text = { Text("支出分类") }
                    )
                    Tab(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        text = { Text("收入分类") }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // 分类列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = categories,
                        key = { _, category -> category.id }
                    ) { index, category ->
                        CategoryListItem(
                            category = category,
                            isDragging = draggingIndex == index,
                            dragOffset = if (draggingIndex == index) dragOffsetY else 0f,
                            onEdit = { viewModel.showEditDialog(category) },
                            onDelete = { viewModel.deleteCategory(category) },
                            onDragStart = {
                                draggingIndex = index
                                dragOffsetY = 0f
                            },
                            onDragEnd = { targetIndex ->
                                if (draggingIndex >= 0 && targetIndex != draggingIndex) {
                                    viewModel.moveCategory(draggingIndex, targetIndex)
                                }
                                draggingIndex = -1
                                dragOffsetY = 0f
                            },
                            onDragChange = { offset ->
                                if (draggingIndex == index) {
                                    dragOffsetY = offset
                                    // 计算目标位置
                                    val itemHeight = 72f
                                    val targetIndex = index + (offset / itemHeight).roundToInt()
                                    if (targetIndex != index && targetIndex >= 0 && targetIndex < categories.size) {
                                        // 可以在这里实时更新位置
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // 添加对话框
        if (uiState.showAddDialog) {
            CategoryDialog(
                title = "添加${if (uiState.dialogType == TransactionType.EXPENSE) "支出" else "收入"}分类",
                name = uiState.dialogName,
                color = uiState.dialogColor,
                onNameChange = { viewModel.setDialogName(it) },
                onColorChange = { viewModel.setDialogColor(it) },
                onConfirm = { viewModel.addCategory() },
                onDismiss = { viewModel.hideAddDialog() }
            )
        }

        // 编辑对话框
        if (uiState.showEditDialog && uiState.editingCategory != null) {
            CategoryDialog(
                title = "编辑分类",
                name = uiState.dialogName,
                color = uiState.dialogColor,
                onNameChange = { viewModel.setDialogName(it) },
                onColorChange = { viewModel.setDialogColor(it) },
                onConfirm = { viewModel.updateCategory() },
                onDismiss = { viewModel.hideEditDialog() }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryDialog(
    title: String,
    name: String,
    color: String,
    onNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // 将当前颜色转换为 HSV
    var hue by remember(color) {
        mutableFloatStateOf(colorToHue(color))
    }
    var customColorMode by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                // 名称输入
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("分类名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // 颜色预览
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(color)))
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "当前颜色: $color",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(12.dp))

                // 预设颜色快捷选择
                Text(
                    text = "预设颜色",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presetColors = listOf(
                        "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4",
                        "#FFEAA7", "#DDA0DD", "#98D8C8", "#808080",
                        "#FF9800", "#E91E63", "#9C27B0", "#673AB7"
                    )
                    presetColors.forEach { colorHex ->
                        val isSelected = color == colorHex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                .clickable {
                                    onColorChange(colorHex)
                                    customColorMode = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Filled.Check,
                                    "选中",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 自定义颜色
                Text(
                    text = "自定义颜色",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(8.dp))

                // 色相滑块
                Text(
                    text = "色相",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))

                // 色相渐变背景的滑块
                Slider(
                    value = hue,
                    onValueChange = { newHue ->
                        hue = newHue
                        val newColor = hueToHex(newHue)
                        onColorChange(newColor)
                        customColorMode = true
                    },
                    valueRange = 0f..360f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // 十六进制颜色输入
                OutlinedTextField(
                    value = color,
                    onValueChange = { input ->
                        // 验证是否是有效的十六进制颜色
                        val cleaned = input.uppercase().replace("#", "")
                        if (cleaned.length <= 6 && cleaned.all { it in '0'..'9' || it in 'A'..'F' }) {
                            val newColor = "#$cleaned"
                            if (cleaned.length == 6) {
                                onColorChange(newColor)
                                hue = colorToHue(newColor)
                            }
                        }
                    },
                    label = { Text("十六进制颜色 (如 FF6B6B)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("#") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = name.isNotBlank() && color.length == 7
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// 将十六进制颜色转换为色相值
private fun colorToHue(colorHex: String): Float {
    try {
        val color = android.graphics.Color.parseColor(colorHex)
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color, hsv)
        return hsv[0]
    } catch (e: Exception) {
        return 0f
    }
}

// 将色相值转换为十六进制颜色 (饱和度和亮度固定为较鲜艳的值)
private fun hueToHex(hue: Float): String {
    val hsv = FloatArray(3)
    hsv[0] = hue
    hsv[1] = 0.7f  // 饱和度
    hsv[2] = 0.8f  // 亮度
    val color = android.graphics.Color.HSVToColor(hsv)
    return String.format("#%06X", (0xFFFFFF and color))
}

@Composable
private fun CategoryListItem(
    category: Category,
    isDragging: Boolean,
    dragOffset: Float,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: (Int) -> Unit,
    onDragChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 1.dp,
        label = "elevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(0, dragOffset.roundToInt()) }
            .shadow(elevation, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDragEnd = {
                        val targetIndex = (dragOffset / 72f).roundToInt()
                        onDragEnd(targetIndex)
                    },
                    onDrag = { change, offset ->
                        change.consume()
                        onDragChange(offset.y)
                    }
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 分类图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(category.color))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name.take(2),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.width(16.dp))

            // 分类名称
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (isDragging) {
                    Text(
                        text = "拖动中...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 拖拽提示图标
            if (isDragging) {
                Icon(
                    Icons.Filled.Reorder,
                    contentDescription = "拖拽",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // 编辑按钮
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "编辑",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // 删除按钮
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}