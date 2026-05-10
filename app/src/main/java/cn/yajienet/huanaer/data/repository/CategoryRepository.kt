package cn.yajienet.huanaer.data.repository

import cn.yajienet.huanaer.data.local.dao.CategoryDao
import cn.yajienet.huanaer.data.local.entity.CategoryEntity
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun insert(category: CategoryEntity): Long {
        return categoryDao.insert(category)
    }

    suspend fun update(category: CategoryEntity) {
        categoryDao.update(category)
    }

    suspend fun delete(category: CategoryEntity) {
        categoryDao.delete(category)
    }

    suspend fun getById(id: Long): Category? {
        return categoryDao.getById(id)?.toCategory()
    }

    fun getAll(): Flow<List<Category>> {
        return categoryDao.getAll().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    fun getByType(type: TransactionType): Flow<List<Category>> {
        return categoryDao.getByType(type).map { entities ->
            entities.map { it.toCategory() }
        }
    }

    fun getDefaultCategories(): Flow<List<Category>> {
        return categoryDao.getDefaultCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    suspend fun updateSortOrder(id: Long, sortOrder: Int) {
        categoryDao.updateSortOrder(id, sortOrder)
    }

    suspend fun updateAllSortOrders(categories: List<Category>) {
        categories.forEachIndexed { index, category ->
            categoryDao.updateSortOrder(category.id, index)
        }
    }

    private fun CategoryEntity.toCategory(): Category {
        return Category(
            id = id,
            name = name,
            icon = icon,
            color = color,
            type = type,
            sortOrder = sortOrder,
            isDefault = isDefault
        )
    }
}