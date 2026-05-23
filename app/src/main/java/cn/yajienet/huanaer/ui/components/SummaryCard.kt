package cn.yajienet.huanaer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.components.glassmorphism.AnimatedNumber
import cn.yajienet.huanaer.ui.components.glassmorphism.GlassCard
import cn.yajienet.huanaer.ui.components.glassmorphism.NeumorphicCard
import cn.yajienet.huanaer.ui.theme.extendedColorScheme
import cn.yajienet.huanaer.util.CurrencyFormat

@Composable
fun SummaryCard(
    totalBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    dailyExpenses: List<Pair<Long, Double>> = emptyList(),
    modifier: Modifier = Modifier
) {
    val extendedColors = extendedColorScheme()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Total Balance - GlassCard (毛玻璃效果)
        GlassCard(
            glassAlpha = 0.8f,
            blurRadius = 25.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "总余额",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedNumber(
                        targetValue = totalBalance,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (dailyExpenses.size >= 2) {
                        Spacer(modifier = Modifier.height(12.dp))
                        SparkLine(
                            data = dailyExpenses.map { it.second },
                            color = extendedColors.expense,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        )
                    }
                }
            }
        }

        // Income / Expense Row - NeubruCards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NeumorphicCard(
                modifier = Modifier.weight(1f),
                containerColor = extendedColors.incomeContainer.copy(alpha = 0.15f),
                contentPadding = 16.dp
            ) {
                Column {
                    Text(
                        text = "收入",
                        style = MaterialTheme.typography.titleSmall,
                        color = extendedColors.income
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¥${CurrencyFormat.format(totalIncome)}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = extendedColors.income
                    )
                }
            }
            NeumorphicCard(
                modifier = Modifier.weight(1f),
                containerColor = extendedColors.expenseContainer.copy(alpha = 0.15f),
                contentPadding = 16.dp
            ) {
                Column {
                    Text(
                        text = "支出",
                        style = MaterialTheme.typography.titleSmall,
                        color = extendedColors.expense
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¥${CurrencyFormat.format(totalExpense)}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = extendedColors.expense
                    )
                }
            }
        }
    }
}

@Composable
private fun SparkLine(
    data: List<Double>,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxVal = data.maxOrNull() ?: return
    val minVal = data.minOrNull() ?: return
    val range = maxVal - minVal
    val safeRange = if (range == 0.0) 1.0 else range

    Canvas(modifier = modifier) {
        val stepX = size.width / (data.size - 1).coerceAtLeast(1)
        val padding = 4.dp.toPx()

        val points = data.mapIndexed { index, value ->
            val x = index * stepX
            val y = size.height - padding - ((value - minVal) / safeRange * (size.height - padding * 2)).toFloat()
            androidx.compose.ui.geometry.Offset(x, y)
        }

        // Gradient fill under the line
        val fillPath = Path().apply {
            moveTo(points.first().x, size.height)
            points.forEach { point -> lineTo(point.x, point.y) }
            lineTo(points.last().x, size.height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.05f)),
                startY = 0f,
                endY = size.height
            )
        )

        // Line
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(
            path = linePath,
            color = color,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // End dot
        if (points.isNotEmpty()) {
            drawCircle(
                color = color,
                radius = 3.dp.toPx(),
                center = points.last()
            )
        }
    }
}