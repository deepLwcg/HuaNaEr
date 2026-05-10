package cn.yajienet.huanaer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import cn.yajienet.huanaer.data.local.entity.BudgetEntity
import cn.yajienet.huanaer.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

// JOIN查询结果：包含预算实体和分类信息
data class BudgetWithCategory(
    @Embedded val budget: BudgetEntity,
    val categoryName: String,
    val categoryColor: String
)

// 批量支出统计结果
data class CategorySpent(val categoryId: Long, val spent: Double)

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: Long): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getByMonthYear(month: Int, year: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year LIMIT 1")
    suspend fun getByCategoryAndMonthYear(categoryId: Long, month: Int, year: Int): BudgetEntity?

    @Query("SELECT * FROM budgets ORDER BY year DESC, month DESC")
    fun getAll(): Flow<List<BudgetEntity>>

    // ===== JOIN查询方法：一次性获取预算和分类信息 =====

    @Query("""
        SELECT b.*, c.name as categoryName, c.color as categoryColor
        FROM budgets b
        LEFT JOIN categories c ON b.categoryId = c.id
        WHERE b.id = :id
    """)
    suspend fun getByIdWithCategory(id: Long): BudgetWithCategory?

    @Query("""
        SELECT b.*, c.name as categoryName, c.color as categoryColor
        FROM budgets b
        LEFT JOIN categories c ON b.categoryId = c.id
        WHERE b.month = :month AND b.year = :year
    """)
    fun getByMonthYearWithCategory(month: Int, year: Int): Flow<List<BudgetWithCategory>>

    @Query("""
        SELECT b.*, c.name as categoryName, c.color as categoryColor
        FROM budgets b
        LEFT JOIN categories c ON b.categoryId = c.id
        ORDER BY b.year DESC, b.month DESC
    """)
    fun getAllWithCategory(): Flow<List<BudgetWithCategory>>

    // ===== 批量支出统计：一次性获取指定月份所有分类的支出 =====

    @Query("""
        SELECT categoryId, SUM(amount) as spent
        FROM transactions
        WHERE type = 'EXPENSE' AND categoryId IN (:categoryIds)
        AND date >= :startTime AND date < :endTime
        GROUP BY categoryId
    """)
    suspend fun getCategorySpents(categoryIds: List<Long>, startTime: Long, endTime: Long): List<CategorySpent>
}