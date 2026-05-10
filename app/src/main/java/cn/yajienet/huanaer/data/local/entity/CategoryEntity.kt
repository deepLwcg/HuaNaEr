package cn.yajienet.huanaer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.yajienet.huanaer.data.model.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val type: TransactionType,
    val sortOrder: Int = 0,
    val isDefault: Boolean = false
)