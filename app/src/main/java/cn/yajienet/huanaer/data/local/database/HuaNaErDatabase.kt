package cn.yajienet.huanaer.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cn.yajienet.huanaer.data.local.dao.BudgetDao
import cn.yajienet.huanaer.data.local.dao.CategoryDao
import cn.yajienet.huanaer.data.local.dao.TransactionDao
import cn.yajienet.huanaer.data.local.entity.BudgetEntity
import cn.yajienet.huanaer.data.local.entity.CategoryEntity
import cn.yajienet.huanaer.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HuaNaErDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: HuaNaErDatabase? = null

        fun getDatabase(context: Context): HuaNaErDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HuaNaErDatabase::class.java,
                    "huanaer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}