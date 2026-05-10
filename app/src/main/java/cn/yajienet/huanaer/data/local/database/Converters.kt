package cn.yajienet.huanaer.data.local.database

import androidx.room.TypeConverter
import cn.yajienet.huanaer.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)
}