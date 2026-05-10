package cn.yajienet.huanaer.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat

/**
 * Display an amount with appropriate color based on transaction type
 */
@Composable
fun AmountDisplay(
    amount: Double,
    type: TransactionType,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    modifier: Modifier = Modifier
) {
    val colors = extendedColorScheme()
    val color = when (type) {
        TransactionType.EXPENSE -> colors.expense
        TransactionType.INCOME -> colors.income
    }

    Text(
        text = CurrencyFormat.format(amount),
        style = style,
        color = color,
        modifier = modifier
    )
}