package cn.yajienet.huanaer.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormat {
    private val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)

    /**
     * 带货币符号，如 ¥1,234.56（NumberFormat 本地化）
     */
    fun format(amount: Double): String = format.format(amount)

    /**
     * 仅数字部分，如 1234.56（用于手动拼接前缀 ¥ 或 +/-）
     */
    fun formatWithoutSymbol(amount: Double): String {
        return String.format(Locale.CHINA, "%.2f", amount)
    }

    /** 单个 ¥ 前缀 + 数字 */
    fun formatWithPrefix(amount: Double, prefix: String = "¥"): String {
        return prefix + formatWithoutSymbol(amount)
    }

    /** 带正负号，如 +¥100.00 / -¥50.00 */
    fun formatSigned(amount: Double, isIncome: Boolean): String {
        val sign = if (isIncome) "+" else "-"
        return sign + formatWithPrefix(kotlin.math.abs(amount))
    }

    /**
     * Parse a currency string to Double
     */
    fun parse(currencyString: String): Double? {
        return try {
            // Remove currency symbol and parse
            val cleanString = currencyString.replace("¥", "").replace("￥", "").trim()
            cleanString.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }
}