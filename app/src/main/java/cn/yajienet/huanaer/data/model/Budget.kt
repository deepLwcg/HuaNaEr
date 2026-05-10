package cn.yajienet.huanaer.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryColor: String = "#808080",
    val amount: Double,
    val month: Int,
    val year: Int,
    val spent: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)