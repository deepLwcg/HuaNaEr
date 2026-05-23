package cn.yajienet.huanaer.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.ui.components.candy.BouncyNumber
import cn.yajienet.huanaer.ui.components.candy.CandyCard
import cn.yajienet.huanaer.ui.theme.CandyTokens
import cn.yajienet.huanaer.ui.theme.LocalAppShapes
import cn.yajienet.huanaer.ui.theme.extendedColorScheme

@Composable
fun SummaryCard(
    totalBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    dailyExpenses: List<Pair<Long, Double>> = emptyList(),
    modifier: Modifier = Modifier
) {
    val extendedColors = extendedColorScheme()
    val shapes = LocalAppShapes.current
    val floatTransition = rememberInfiniteTransition(label = "balance_float")
    val floatY by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "float_y"
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CandyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .offset(y = floatY.dp),
            gradient = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            ),
            contentPadding = 20.dp
        ) {
            Column {
                Text(
                    text = "总余额 ✨",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                BouncyNumber(
                    targetValue = totalBalance,
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (dailyExpenses.size >= 2) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SparkLine(
                        data = dailyExpenses.map { it.second },
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CandyCard(
                modifier = Modifier.weight(1f),
                containerColor = extendedColors.incomeContainer,
                shadowColor = CandyTokens.coloredShadowColor(extendedColors.income),
                shape = shapes.cardMedium,
                elevation = 2.dp,
                contentPadding = 14.dp
            ) {
                Column {
                    Text(
                        text = "收入 · 本月",
                        style = MaterialTheme.typography.titleSmall,
                        color = extendedColors.income
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BouncyNumber(
                        targetValue = totalIncome,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = extendedColors.income
                    )
                }
            }
            CandyCard(
                modifier = Modifier.weight(1f),
                containerColor = extendedColors.expenseContainer,
                shadowColor = CandyTokens.coloredShadowColor(extendedColors.expense),
                shape = shapes.cardMedium,
                elevation = 2.dp,
                contentPadding = 14.dp
            ) {
                Column {
                    Text(
                        text = "支出 · 本月",
                        style = MaterialTheme.typography.titleSmall,
                        color = extendedColors.expense
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BouncyNumber(
                        targetValue = totalExpense,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
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
            Offset(x, y)
        }
        val fillPath = Path().apply {
            moveTo(points.first().x, size.height)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, size.height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.05f)),
                startY = 0f,
                endY = size.height
            )
        )
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
        }
        drawPath(path = linePath, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(color = color, radius = 4.dp.toPx(), center = points.last())
    }
}
