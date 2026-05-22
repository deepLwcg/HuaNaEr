package cn.yajienet.huanaer.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class ThemeStyle {
    MINT_BREEZE,
    SUNSET_GLOW,
    MIDNIGHT_NEON
}

class SettingsDataStore(private val context: Context) {

    companion object {
        // 新版使用 string key 存枚举 name
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode_v2")
        private val THEME_STYLE_KEY = stringPreferencesKey("theme_style_v2")
        // 旧版 int key，用于兼容
        private val THEME_MODE_KEY_LEGACY = intPreferencesKey("theme_mode")
        private val THEME_STYLE_KEY_LEGACY = intPreferencesKey("theme_style")
        // 以下 key 保持不变
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        private val MONTH_START_DAY_KEY = intPreferencesKey("month_start_day")
        private val DEFAULT_EXPENSE_CATEGORY_KEY = longPreferencesKey("default_expense_category")
        private val DEFAULT_INCOME_CATEGORY_KEY = longPreferencesKey("default_income_category")
        private val LARGE_AMOUNT_THRESHOLD_KEY = doublePreferencesKey("large_amount_threshold")
    }

    val themeStyle: Flow<ThemeStyle> = context.dataStore.data.map { preferences ->
        val name = preferences[THEME_STYLE_KEY]
        if (name != null) {
            ThemeStyle.entries.find { it.name == name } ?: ThemeStyle.MINT_BREEZE
        } else {
            val ordinal = preferences[THEME_STYLE_KEY_LEGACY]
            if (ordinal != null) ThemeStyle.entries.getOrElse(ordinal) { ThemeStyle.MINT_BREEZE }
            else ThemeStyle.MINT_BREEZE
        }
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val name = preferences[THEME_MODE_KEY]
        if (name != null) {
            ThemeMode.entries.find { it.name == name } ?: ThemeMode.SYSTEM
        } else {
            val ordinal = preferences[THEME_MODE_KEY_LEGACY]
            if (ordinal != null) ThemeMode.entries.getOrElse(ordinal) { ThemeMode.SYSTEM }
            else ThemeMode.SYSTEM
        }
    }

    val dynamicColor: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_KEY] ?: true
    }

    val monthStartDay: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[MONTH_START_DAY_KEY] ?: 1
    }

    val defaultExpenseCategoryId: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_EXPENSE_CATEGORY_KEY] ?: -1L
    }

    val defaultIncomeCategoryId: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_INCOME_CATEGORY_KEY] ?: -1L
    }

    val largeAmountThreshold: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[LARGE_AMOUNT_THRESHOLD_KEY] ?: -1.0
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
            preferences.remove(THEME_MODE_KEY_LEGACY)
        }
    }

    suspend fun setThemeStyle(style: ThemeStyle) {
        context.dataStore.edit { preferences ->
            preferences[THEME_STYLE_KEY] = style.name
            preferences.remove(THEME_STYLE_KEY_LEGACY)
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }

    suspend fun setMonthStartDay(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[MONTH_START_DAY_KEY] = day.coerceIn(1, 28)
        }
    }

    suspend fun setDefaultExpenseCategoryId(categoryId: Long) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_EXPENSE_CATEGORY_KEY] = categoryId
        }
    }

    suspend fun setDefaultIncomeCategoryId(categoryId: Long) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_INCOME_CATEGORY_KEY] = categoryId
        }
    }

    suspend fun setLargeAmountThreshold(threshold: Double) {
        context.dataStore.edit { preferences ->
            preferences[LARGE_AMOUNT_THRESHOLD_KEY] = threshold
        }
    }
}