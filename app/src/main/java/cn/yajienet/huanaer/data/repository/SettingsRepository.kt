package cn.yajienet.huanaer.data.repository

import cn.yajienet.huanaer.data.datastore.SettingsDataStore
import cn.yajienet.huanaer.data.datastore.ThemeMode
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsDataStore: SettingsDataStore) {

    val themeMode: Flow<ThemeMode> = settingsDataStore.themeMode
    val dynamicColor: Flow<Boolean> = settingsDataStore.dynamicColor
    val monthStartDay: Flow<Int> = settingsDataStore.monthStartDay
    val defaultExpenseCategoryId: Flow<Long> = settingsDataStore.defaultExpenseCategoryId
    val defaultIncomeCategoryId: Flow<Long> = settingsDataStore.defaultIncomeCategoryId
    val largeAmountThreshold: Flow<Double> = settingsDataStore.largeAmountThreshold

    suspend fun setThemeMode(mode: ThemeMode) {
        settingsDataStore.setThemeMode(mode)
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        settingsDataStore.setDynamicColor(enabled)
    }

    suspend fun setMonthStartDay(day: Int) {
        settingsDataStore.setMonthStartDay(day)
    }

    suspend fun setDefaultExpenseCategoryId(categoryId: Long) {
        settingsDataStore.setDefaultExpenseCategoryId(categoryId)
    }

    suspend fun setDefaultIncomeCategoryId(categoryId: Long) {
        settingsDataStore.setDefaultIncomeCategoryId(categoryId)
    }

    suspend fun setLargeAmountThreshold(threshold: Double) {
        settingsDataStore.setLargeAmountThreshold(threshold)
    }
}