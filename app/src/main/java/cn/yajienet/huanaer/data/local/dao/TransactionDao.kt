package cn.yajienet.huanaer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import cn.yajienet.huanaer.data.local.entity.CategoryEntity
import cn.yajienet.huanaer.data.local.entity.TransactionEntity
import cn.yajienet.huanaer.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

data class CategoryTotal(val categoryId: Long, val total: Double)
data class DailyTotal(val day: Long, val total: Double)

// JOIN查询结果：包含交易实体和分类信息
data class TransactionWithCategory(
    @Embedded val transaction: TransactionEntity,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String
)

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE date >= :startTime AND date < :endTime
        ORDER BY date DESC
    """)
    fun getByDateRange(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE categoryId = :categoryId
        ORDER BY date DESC
    """)
    fun getByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE type = :type AND date >= :startTime AND date < :endTime
    """)
    fun getTotalByTypeAndDateRange(type: TransactionType, startTime: Long, endTime: Long): Flow<Double?>

    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE type = :type AND categoryId = :categoryId
        AND date >= :startTime AND date < :endTime
    """)
    fun getTotalByCategoryAndDateRange(
        type: TransactionType,
        categoryId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<Double?>

    @Query("""
        SELECT categoryId, SUM(amount) as total
        FROM transactions
        WHERE type = :type AND date >= :startTime AND date < :endTime
        GROUP BY categoryId
    """)
    fun getCategoryTotals(type: TransactionType, startTime: Long, endTime: Long): Flow<List<CategoryTotal>>

    @Query("""
        SELECT date/86400000 as day, SUM(amount) as total
        FROM transactions
        WHERE type = :type AND date >= :startTime AND date < :endTime
        GROUP BY day
    """)
    fun getDailyTotals(type: TransactionType, startTime: Long, endTime: Long): Flow<List<DailyTotal>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    // ===== JOIN查询方法：一次性获取交易和分类信息 =====

    @Query("""
        SELECT t.*, c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM transactions t
        LEFT JOIN categories c ON t.categoryId = c.id
        WHERE t.id = :id
    """)
    suspend fun getByIdWithCategory(id: Long): TransactionWithCategory?

    @Query("""
        SELECT t.*, c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM transactions t
        LEFT JOIN categories c ON t.categoryId = c.id
        ORDER BY t.date DESC
    """)
    fun getAllWithCategory(): Flow<List<TransactionWithCategory>>

    @Query("""
        SELECT t.*, c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM transactions t
        LEFT JOIN categories c ON t.categoryId = c.id
        WHERE t.date >= :startTime AND t.date < :endTime
        ORDER BY t.date DESC
    """)
    fun getByDateRangeWithCategory(startTime: Long, endTime: Long): Flow<List<TransactionWithCategory>>

    @Query("""
        SELECT t.*, c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM transactions t
        LEFT JOIN categories c ON t.categoryId = c.id
        WHERE t.categoryId = :categoryId
        ORDER BY t.date DESC
    """)
    fun getByCategoryWithCategory(categoryId: Long): Flow<List<TransactionWithCategory>>

    @Query("""
        SELECT t.*, c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM transactions t
        LEFT JOIN categories c ON t.categoryId = c.id
        ORDER BY t.date DESC LIMIT :limit
    """)
    fun getRecentTransactionsWithCategory(limit: Int): Flow<List<TransactionWithCategory>>
}