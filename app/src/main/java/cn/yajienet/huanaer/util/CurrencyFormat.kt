package cn.yajienet.huanaer.util

import java.util.Locale

object CurrencyFormat {

    /** 应用内统一使用的人民币符号（与中文环境常见展示一致） */
    const val SYMBOL = "￥"

    private val HALF_WIDTH_SYMBOL = "¥"

    /**
     * 带货币符号，如 ￥1,234.56
     */
    fun format(amount: Double): String = formatWithPrefix(amount)

    /**
     * 仅数字部分，如 1234.56（用于输入框或手动拼接符号）
     */
    fun formatWithoutSymbol(amount: Double): String {
        return String.format(Locale.CHINA, "%,.2f", amount)
    }

    /** 货币符号 + 数字（默认两位小数、千分位） */
    fun formatWithPrefix(amount: Double, prefix: String = SYMBOL): String {
        return prefix + formatWithoutSymbol(amount)
    }

    /** 带正负号，如 +￥100.00 / -￥50.00 */
    fun formatSigned(amount: Double, isIncome: Boolean): String {
        val sign = if (isIncome) "+" else "-"
        return sign + formatWithPrefix(kotlin.math.abs(amount))
    }

    /**
     * 解析含货币符号或千分位的字符串
     */
    fun parse(currencyString: String): Double? {
        val clean = stripCurrencyAndGrouping(currencyString)
        return clean.toDoubleOrNull()
    }

    /**
     * 输入框清洗：去掉符号、千分位，中文句号转小数点，仅保留数字与小数点
     */
    fun sanitizeAmountInput(input: String): String {
        val normalized = stripCurrencyAndGrouping(input)
            .replace("。", ".")
        val result = StringBuilder()
        var hasDot = false
        var decimalCount = 0
        var integerCount = 0
        for (ch in normalized) {
            when {
                ch.isDigit() && !hasDot -> {
                    if (integerCount < MAX_INTEGER_DIGITS) {
                        result.append(ch)
                        integerCount++
                    }
                }
                ch.isDigit() && hasDot -> {
                    if (decimalCount < 2) {
                        result.append(ch)
                        decimalCount++
                    }
                }
                ch == '.' && !hasDot -> {
                    hasDot = true
                    result.append('.')
                }
            }
        }
        return result.toString()
    }

    private fun stripCurrencyAndGrouping(input: String): String {
        return input
            .replace(SYMBOL, "")
            .replace(HALF_WIDTH_SYMBOL, "")
            .replace("元", "")
            .replace(" ", "")
            .replace(",", "")
            .replace("，", "")
            .trim()
    }

    private const val MAX_INTEGER_DIGITS = 9
}
