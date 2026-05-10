package cn.yajienet.huanaer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat

@Composable
fun SummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    balance: Double,
    modifier: Modifier = Modifier
) {
    val colors = extendedColorScheme()
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "本月概览",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Balance
            Text(
                text = CurrencyFormat.format(balance),
                style = MaterialTheme.typography.displaySmall,
                color = if (balance >= 0) colors.income else colors.expense
            )
            Text(
                text = if (balance >= 0) "结余" else "亏损",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Income and Expense
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Income column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .background(
                                colors.incomeContainer.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "收入",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.onIncomeContainer
                            )
                            Text(
                                text = CurrencyFormat.format(totalIncome),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.income,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Expense column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .background(
                                colors.expenseContainer.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "支出",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.onExpenseContainer
                            )
                            Text(
                                text = CurrencyFormat.format(totalExpense),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.expense,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}