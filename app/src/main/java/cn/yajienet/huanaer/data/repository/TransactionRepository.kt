package cn.yajienet.huanaer.data.repository

import cn.yajienet.huanaer.data.local.dao.TransactionDao
import cn.yajienet.huanaer.data.local.dao.CategoryDao
import cn.yajienet.huanaer.data.local.entity.TransactionEntity
import cn.yajienet.huanaer.data.model.Transaction
import cn.yajienet.huanaer.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
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
        val entity = transactionDao.getById(id)
        return entity?.toTransaction()
    }

    fun getAll(): Flow<List<Transaction>> {
        return transactionDao.getAll().map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getByDateRange(startTime: Long, endTime: Long): Flow<List<Transaction>> {
        return transactionDao.getByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getByCategory(categoryId: Long): Flow<List<Transaction>> {
        return transactionDao.getByCategory(categoryId).map { entities ->
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

    fun getDailyTotals(type: TransactionType, startTime: Long, endTime: Long): Flow<Map<Long, Double>> {
        return transactionDao.getDailyTotals(type, startTime, endTime).map { totals ->
            totals.associate { it.day to it.total }
        }
    }

    fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    private suspend fun TransactionEntity.toTransaction(): Transaction {
        val category = categoryDao.getById(categoryId)
        return Transaction(
            id = id,
            amount = amount,
            type = type,
            categoryId = categoryId,
            categoryName = category?.name ?: "",
            categoryIcon = category?.icon ?: "",
            categoryColor = category?.color ?: "#808080",
            date = date,
            note = note,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}