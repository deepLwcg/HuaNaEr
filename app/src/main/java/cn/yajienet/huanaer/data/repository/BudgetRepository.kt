package cn.yajienet.huanaer.data.repository

import cn.yajienet.huanaer.data.local.dao.BudgetDao
import cn.yajienet.huanaer.data.local.dao.BudgetWithCategory
import cn.yajienet.huanaer.data.local.dao.CategorySpent
import cn.yajienet.huanaer.data.local.entity.BudgetEntity
import cn.yajienet.huanaer.data.model.Budget
import cn.yajienet.huanaer.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import cn.yajienet.huanaer.data.local.dao.TransactionDao

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {

    suspend fun insert(budget: BudgetEntity): Long {
        return budgetDao.insert(budget)
    }

    suspend fun update(budget: BudgetEntity) {
        budgetDao.update(budget)
    }

    suspend fun delete(budget: BudgetEntity) {
        budgetDao.delete(budget)
    }

    suspend fun getById(id: Long): Budget? {
        val result = budgetDao.getByIdWithCategory(id)
        if (result == null) return null

        val spent = getSpentAmount(result.budget.categoryId, result.budget.month, result.budget.year)
        return result.toBudget(spent)
    }

    fun getByMonthYear(month: Int, year: Int): Flow<List<Budget>> {
        return budgetDao.getByMonthYearWithCategory(month, year).map { entities ->
            // 批量获取支出金额（同一月份，可以批量查询）
            val categoryIds = entities.map { it.budget.categoryId }.distinct()
            val startTime = getMonthStartTime(month, year)
            val endTime = getMonthEndTime(month, year)

            if (categoryIds.isEmpty()) {
                emptyList()
            } else {
                // 需要在 Flow.map 中进行 suspend 操作，改用 first 获取
                val spents = getCategorySpentsBatch(categoryIds, startTime, endTime)
                entities.map { entity ->
                    val spent = spents[entity.budget.categoryId] ?: 0.0
                    entity.toBudget(spent)
                }
            }
        }
    }

    suspend fun getByCategoryAndMonthYear(categoryId: Long, month: Int, year: Int): Budget? {
        val budgets = budgetDao.getByMonthYearWithCategory(month, year).first()
        val entity = budgets.find { it.budget.categoryId == categoryId }
        if (entity == null) return null

        val spent = getSpentAmount(categoryId, month, year)
        return entity.toBudget(spent)
    }

    fun getAll(): Flow<List<Budget>> {
        return budgetDao.getAllWithCategory().map { entities ->
            // 按月份分组，批量计算支出
            val groupedByMonth = entities.groupBy { Pair(it.budget.month, it.budget.year) }

            groupedByMonth.flatMap { (monthYear, monthEntities) ->
                val (month, year) = monthYear
                val categoryIds = monthEntities.map { it.budget.categoryId }.distinct()
                val startTime = getMonthStartTime(month, year)
                val endTime = getMonthEndTime(month, year)

                val spents = if (categoryIds.isEmpty()) {
                    emptyMap()
                } else {
                    getCategorySpentsBatch(categoryIds, startTime, endTime)
                }

                monthEntities.map { entity ->
                    val spent = spents[entity.budget.categoryId] ?: 0.0
                    entity.toBudget(spent)
                }
            }
        }
    }

    private suspend fun getSpentAmount(categoryId: Long, month: Int, year: Int): Double {
        val startTime = getMonthStartTime(month, year)
        val endTime = getMonthEndTime(month, year)
        val totalFlow = transactionDao.getTotalByCategoryAndDateRange(
            TransactionType.EXPENSE,
            categoryId,
            startTime,
            endTime
        )
        return totalFlow.map { it ?: 0.0 }.first()
    }

    private suspend fun getCategorySpentsBatch(
        categoryIds: List<Long>,
        startTime: Long,
        endTime: Long
    ): Map<Long, Double> {
        val spents = budgetDao.getCategorySpents(categoryIds, startTime, endTime)
        return spents.associate { it.categoryId to it.spent }
    }

    private fun BudgetWithCategory.toBudget(spent: Double): Budget {
        return Budget(
            id = budget.id,
            categoryId = budget.categoryId,
            categoryName = categoryName ?: "总预算",
            categoryColor = categoryColor ?: "#808080",
            amount = budget.amount,
            month = budget.month,
            year = budget.year,
            spent = spent,
            createdAt = budget.createdAt
        )
    }

    private fun getMonthStartTime(month: Int, year: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getMonthEndTime(month: Int, year: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        calendar.add(java.util.Calendar.MONTH, 1)
        return calendar.timeInMillis
    }
}