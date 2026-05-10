package cn.yajienet.huanaer.data.repository

import cn.yajienet.huanaer.data.local.dao.BudgetDao
import cn.yajienet.huanaer.data.local.dao.CategoryDao
import cn.yajienet.huanaer.data.local.dao.TransactionDao
import cn.yajienet.huanaer.data.local.entity.BudgetEntity
import cn.yajienet.huanaer.data.model.Budget
import cn.yajienet.huanaer.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
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
        return budgetDao.getById(id)?.toBudget()
    }

    fun getByMonthYear(month: Int, year: Int): Flow<List<Budget>> {
        return budgetDao.getByMonthYear(month, year).map { entities ->
            entities.map { it.toBudget() }
        }
    }

    suspend fun getByCategoryAndMonthYear(categoryId: Long, month: Int, year: Int): Budget? {
        return budgetDao.getByCategoryAndMonthYear(categoryId, month, year)?.toBudget()
    }

    fun getAll(): Flow<List<Budget>> {
        return budgetDao.getAll().map { entities ->
            entities.map { it.toBudget() }
        }
    }

    private suspend fun BudgetEntity.toBudget(): Budget {
        val category = categoryDao.getById(categoryId)
        val spent = getSpentAmount(categoryId, month, year)
        return Budget(
            id = id,
            categoryId = categoryId,
            categoryName = category?.name ?: "总预算",
            categoryColor = category?.color ?: "#808080",
            amount = amount,
            month = month,
            year = year,
            spent = spent,
            createdAt = createdAt
        )
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