package cn.yajienet.huanaer.ui.screens.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.components.AmountInputField
import cn.yajienet.huanaer.ui.components.candy.ConfettiBurst
import cn.yajienet.huanaer.ui.components.candy.EmojiCategoryChip
import cn.yajienet.huanaer.ui.components.candy.SegmentedGummy
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CategoryEmoji
import cn.yajienet.huanaer.util.CurrencyFormat
import cn.yajienet.huanaer.util.DateUtils
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

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
    val colors = extendedColorScheme()
    val saveTint = if (uiState.type == TransactionType.EXPENSE) colors.expense else colors.income

    LaunchedEffect(uiState.error) {
        val message = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加交易") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveTransaction() },
                        enabled = !uiState.isSaving && !uiState.saved
                    ) {
                        Text(
                            text = if (uiState.isSaving) "保存中" else "保存",
                            color = saveTint,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AddTransactionContent(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding),
            compact = false,
            showSaveAction = false,
            onSaved = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showSaveAction: Boolean = true,
    onSaved: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    val amountFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val colors = extendedColorScheme()

    // 全屏页自动聚焦金额；弹窗不自动弹键盘，避免挡住底部保存按钮
    LaunchedEffect(Unit) {
        if (!compact && !uiState.saved) {
            amountFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            keyboardController?.hide()
            showCelebration = true
        }
    }

    val amountColor = if (uiState.type == TransactionType.EXPENSE) colors.expense else colors.income
    val errorMessage: @Composable () -> Unit = {
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

    val onSave = {
        focusManager.clearFocus()
        keyboardController?.hide()
        viewModel.saveTransaction()
    }

    val saveAction: @Composable () -> Unit = {
        if (showSaveAction) {
            TextButton(
                onClick = onSave,
                enabled = !uiState.isSaving && !uiState.saved
            ) {
                Text(
                    text = if (uiState.isSaving) "保存中" else "保存",
                    color = amountColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    val formFields: @Composable () -> Unit = {
        SegmentedGummy(
                options = listOf("支出", "收入"),
                selectedIndex = if (uiState.type == TransactionType.EXPENSE) 0 else 1,
                onSelected = { index ->
                    viewModel.setType(if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME)
                },
                selectedColor = amountColor
            )

            AmountInputField(
                amount = uiState.amount,
                onAmountChange = viewModel::setAmount,
                accentColor = amountColor,
                focusRequester = amountFocusRequester,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 28.sp else 32.sp
                )
            )

            Column {
                Text(
                    "分类",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        DateUtils.formatDate(uiState.date),
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "备注",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    BasicTextField(
                        value = uiState.note,
                        onValueChange = viewModel::setNote,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box {
                                if (uiState.note.isEmpty()) {
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

    Box(modifier = modifier) {
        if (compact) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val sheetHeight = maxHeight * 0.88f
                Column(
                    modifier = Modifier
                        .height(sheetHeight)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        formFields()
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f)) { errorMessage() }
                        saveAction()
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                formFields()
                errorMessage()
            }
        }

        ConfettiBurst(
            trigger = showCelebration,
            onFinished = {
                showCelebration = false
                if (compact) viewModel.resetForm()
                onSaved?.invoke()
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let(viewModel::setDate)
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
    sheetKey: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as cn.yajienet.huanaer.HuaNaErApplication
    val viewModel: TransactionViewModel = viewModel(
        key = "add_transaction_sheet_$sheetKey",
        factory = TransactionViewModelFactory(application)
    )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    DisposableEffect(Unit) {
        onDispose { viewModel.resetForm() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = LocalAppShapes.current.bottomSheet,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        AddTransactionContent(
            viewModel = viewModel,
            compact = true,
            onSaved = onDismiss
        )
    }
}
