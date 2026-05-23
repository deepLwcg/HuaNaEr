package cn.yajienet.huanaer.util

/**
 * 将分类 icon 字段（Material 图标名或 emoji）解析为展示用 emoji
 */
object CategoryEmoji {
    private val iconToEmoji = mapOf(
        "restaurant" to "🍜",
        "directions_car" to "🚗",
        "shopping_cart" to "🛒",
        "movie" to "🎬",
        "local_hospital" to "🏥",
        "school" to "📚",
        "home" to "🏠",
        "more_horiz" to "✨",
        "account_balance_wallet" to "💰",
        "card_giftcard" to "🎁",
        "trending_up" to "📈",
        "work" to "💼"
    )

    private val nameToEmoji = mapOf(
        "餐饮" to "🍜",
        "交通" to "🚗",
        "购物" to "🛒",
        "娱乐" to "🎬",
        "医疗" to "🏥",
        "教育" to "📚",
        "住房" to "🏠",
        "工资" to "💰",
        "奖金" to "🎁",
        "投资" to "📈",
        "兼职" to "💼"
    )

    fun resolve(icon: String, name: String): String {
        if (icon.any { it.code > 0x200 }) return icon
        return iconToEmoji[icon] ?: nameToEmoji[name] ?: name.take(1)
    }
}
