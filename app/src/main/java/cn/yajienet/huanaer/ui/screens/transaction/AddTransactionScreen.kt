package cn.yajienet.huanaer.ui.screens.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.components.candy.CandyCard
import cn.yajienet.huanaer.ui.components.candy.ConfettiBurst
import cn.yajienet.huanaer.ui.components.candy.EmojiCategoryChip
import cn.yajienet.huanaer.ui.components.candy.GummyButton
import cn.yajienet.huanaer.ui.components.candy.GummyNumberPad
import cn.yajienet.huanaer.ui.components.candy.SegmentedGummy
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.util.CategoryEmoji
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(application)
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            delay(800)
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加交易") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AddTransactionContent(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier,
    onSaved: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.date
    )
    var showCelebration by remember { mutableStateOf(false) }

    // 本地管理金额字符串，避免与 ViewModel 状态同步延迟
    var amountText by remember { mutableStateOf("") }
    // ViewModel 金额变化时同步到本地（如重置）
    LaunchedEffect(uiState.amount) {
        if (uiState.amount != amountText) {
            amountText = uiState.amount
        }
    }

    // 金额字符串由 ViewModel 管理，数字键盘直接操作
    // 保存成功时触发庆祝动画
    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            showCelebration = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 类型切换 — SegmentedSwitch
            val colors = extendedColorScheme()
            SegmentedGummy(
                options = listOf("支出", "收入"),
                selectedIndex = if (uiState.type == TransactionType.EXPENSE) 0 else 1,
                onSelected = { index ->
                    viewModel.setType(if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME)
                },
                selectedColor = if (uiState.type == TransactionType.EXPENSE) colors.expense else colors.income
            )

            CandyCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "金额",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (amountText.isBlank()) "¥0.00"
                               else "¥$amountText",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (uiState.type == TransactionType.EXPENSE)
                            colors.expense
                        else colors.income
                    )
                }
            }

            // 分类选择 — LazyRow + CategoryCircleButton
            Column {
                Text(
                    "分类",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.categories, key = { it.id }) { category ->
                        val selected = uiState.selectedCategoryId == category.id
                        val bgColor = try {
                            Color(category.color.removePrefix("#").toLong(16) or 0xFF000000)
                        } catch (_: Exception) {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                        EmojiCategoryChip(
                            emoji = CategoryEmoji.resolve(category.icon, category.name),
                            selected = selected,
                            onClick = { viewModel.setCategory(category.id) },
                            backgroundColor = bgColor.copy(alpha = if (selected) 0.5f else 0.25f),
                            glowColor = bgColor
                        )
                    }
                }
            }

            // 日期选择
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        dateFormat.format(uiState.date),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "选择日期",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 备注输入
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    var noteText by remember(uiState.note) { mutableStateOf(uiState.note) }
                    Column {
                        Text(
                            "备注",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        BasicTextField(
                            value = noteText,
                            onValueChange = { newText ->
                                noteText = newText
                                viewModel.setNote(newText)
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box {
                                    if (noteText.isEmpty()) {
                                        Text(
                                            "添加备注...",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // 数字键盘
            GummyNumberPad(
                onDigit = { digit ->
                    val current = amountText
                    // 限制小数点后两位
                    if (current.contains(".")) {
                        val afterDot = current.substringAfter(".")
                        if (afterDot.length >= 2) return@GummyNumberPad
                    }
                    // 限制首位为0时只能输入小数点
                    val newAmount = if (current == "0") {
                        digit.toString()
                    } else {
                        current + digit.toString()
                    }
                    amountText = newAmount
                    viewModel.setAmount(newAmount)
                },
                onDecimal = {
                    val newAmount = when {
                        amountText.isEmpty() -> "0."
                        !amountText.contains(".") -> "$amountText."
                        else -> return@GummyNumberPad
                    }
                    amountText = newAmount
                    viewModel.setAmount(newAmount)
                },
                onDelete = {
                    if (amountText.isNotEmpty()) {
                        val newAmount = amountText.dropLast(1)
                        amountText = newAmount
                        viewModel.setAmount(newAmount)
                    }
                },
                onDone = { viewModel.saveTransaction() }
            )

            // 错误提示
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // 庆祝动画 — CelebrationGlow
        ConfettiBurst(
            trigger = showCelebration,
            onFinished = {
                showCelebration = false
                onSaved?.invoke()
            }
        )
    }

    // 日期选择对话框
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { date ->
                            viewModel.setDate(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(application)
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = LocalAppShapes.current.bottomSheet,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        AddTransactionContent(
            viewModel = viewModel,
            onSaved = {
                // 保存成功后关闭底部弹窗
                onDismiss()
            }
        )
    }
}
