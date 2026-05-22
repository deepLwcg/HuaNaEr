package cn.yajienet.huanaer.data.repository

import cn.yajienet.huanaer.data.local.dao.DailyTotal
import cn.yajienet.huanaer.data.local.dao.TransactionDao
import cn.yajienet.huanaer.data.local.dao.TransactionWithCategory
import cn.yajienet.huanaer.data.local.entity.TransactionEntity
import cn.yajienet.huanaer.data.model.Transaction
import cn.yajienet.huanaer.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionDao: TransactionDao
) {
    suspend fun insert(transaction: TransactionEntity): Long {
        return transactionDao.insert(transaction)
    }

    suspend fun update(transaction: TransactionEntity) {
        transactionDao.update(transaction)
    }

    suspend fun delete(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        val entity = TransactionEntity(
            id = transaction.id,
            amount = transaction.amount,
            type = transaction.type,
            categoryId = transaction.categoryId,
            date = transaction.date,
            note = transaction.note,
            createdAt = transaction.createdAt,
            updatedAt = transaction.updatedAt
        )
        transactionDao.delete(entity)
    }

    suspend fun getById(id: Long): Transaction? {
        val result = transactionDao.getByIdWithCategory(id)
        return result?.toTransaction()
    }

    fun getAll(): Flow<List<Transaction>> {
        return transactionDao.getAllWithCategory().map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getByDateRange(startTime: Long, endTime: Long): Flow<List<Transaction>> {
        return transactionDao.getByDateRangeWithCategory(startTime, endTime).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getByCategory(categoryId: Long): Flow<List<Transaction>> {
        return transactionDao.getByCategoryWithCategory(categoryId).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getTotalByTypeAndDateRange(type: TransactionType, startTime: Long, endTime: Long): Flow<Double> {
        return transactionDao.getTotalByTypeAndDateRange(type, startTime, endTime).map { it ?: 0.0 }
    }

    fun getTotalByCategoryAndDateRange(
        type: TransactionType,
        categoryId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<Double> {
        return transactionDao.getTotalByCategoryAndDateRange(type, categoryId, startTime, endTime).map { it ?: 0.0 }
    }

    fun getCategoryTotals(type: TransactionType, startTime: Long, endTime: Long): Flow<Map<Long, Double>> {
        return transactionDao.getCategoryTotals(type, startTime, endTime).map { totals ->
            totals.associate { it.categoryId to it.total }
        }
    }

    fun getDailyTotalsAsPairs(type: TransactionType, startTime: Long, endTime: Long): Flow<List<Pair<Long, Double>>> {
        return transactionDao.getDailyTotals(type, startTime, endTime).map { totals ->
            totals.map { it.day * 86400000L to it.total }
        }
    }

    fun getDailyTotals(type: TransactionType, startTime: Long, endTime: Long): Flow<Map<Long, Double>> {
        return transactionDao.getDailyTotals(type, startTime, endTime).map { totals ->
            totals.associate { it.day to it.total }
        }
    }

    fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactionsWithCategory(limit).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    // 从JOIN查询结果转换为Transaction模型（无需额外查询）
    private fun TransactionWithCategory.toTransaction(): Transaction {
        return Transaction(
            id = transaction.id,
            amount = transaction.amount,
            type = transaction.type,
            categoryId = transaction.categoryId,
            categoryName = categoryName,
            categoryIcon = categoryIcon,
            categoryColor = categoryColor,
            date = transaction.date,
            note = transaction.note,
            createdAt = transaction.createdAt,
            updatedAt = transaction.updatedAt
        )
    }
}