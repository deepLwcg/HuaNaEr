package cn.yajienet.huanaer.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormat {
    private val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)

    /**
     * Format an amount to currency string (¥X.XX)
     */
    fun format(amount: Double): String {
        return format.format(amount)
    }

    /**
     * Format an amount without currency symbol
     */
    fun formatWithoutSymbol(amount: Double): String {
        return String.format(Locale.CHINA, "%.2f", amount)
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