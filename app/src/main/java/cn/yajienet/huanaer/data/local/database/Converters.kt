package cn.yajienet.huanaer.data.local.database

import androidx.room.TypeConverter
import cn.yajienet.huanaer.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType =
        try { TransactionType.valueOf(value) } catch (_: IllegalArgumentException) { TransactionType.EXPENSE }
}
