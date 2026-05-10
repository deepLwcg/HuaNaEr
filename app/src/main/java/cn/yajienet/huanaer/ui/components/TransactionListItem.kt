package cn.yajienet.huanaer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.data.model.Transaction
import cn.yajienet.huanaer.data.model.TransactionType
import cn.yajienet.huanaer.ui.theme.IncomeGreen
import cn.yajienet.huanaer.ui.theme.ExpenseRed
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)
    val dateFormat = SimpleDateFormat("MM-dd", Locale.CHINA)
    val amountColor = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
    val amountPrefix = if (transaction.type == TransactionType.INCOME) "+" else "-"

    ListItem(
        headlineContent = {
            Text(
                text = transaction.categoryName,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = transaction.note ?: dateFormat.format(transaction.date),
                style = MaterialTheme.typography.bodySmall
            )
        },
        trailingContent = {
            Text(
                text = "$amountPrefix${currencyFormat.format(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = amountColor
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable(onClick = onClick)
    )
}