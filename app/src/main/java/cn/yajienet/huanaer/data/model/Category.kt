package cn.yajienet.huanaer.data.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val type: TransactionType,
    val sortOrder: Int = 0,
    val isDefault: Boolean = false
)