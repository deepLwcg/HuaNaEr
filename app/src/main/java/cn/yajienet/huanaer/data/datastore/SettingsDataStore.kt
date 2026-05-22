package cn.yajienet.huanaer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    LIGHT,      // 浅色模式
    DARK,       // 深色模式
    SYSTEM      // 跟随系统
}

enum class ThemeStyle {
    MINT_BREEZE,    // 薄荷清风
    SUNSET_GLOW,    // 落日余晖
    MIDNIGHT_NEON   // 午夜霓虹
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val THEME_MODE_KEY = intPreferencesKey("theme_mode")
        private val THEME_STYLE_KEY = intPreferencesKey("theme_style")
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        private val MONTH_START_DAY_KEY = intPreferencesKey("month_start_day")
        private val DEFAULT_EXPENSE_CATEGORY_KEY = longPreferencesKey("default_expense_category")
        private val DEFAULT_INCOME_CATEGORY_KEY = longPreferencesKey("default_income_category")
        private val LARGE_AMOUNT_THRESHOLD_KEY = doublePreferencesKey("large_amount_threshold")
    }

    val themeStyle: Flow<ThemeStyle> = context.dataStore.data.map { preferences ->
        val ordinal = preferences[THEME_STYLE_KEY] ?: ThemeStyle.MINT_BREEZE.ordinal
        ThemeStyle.entries.getOrElse(ordinal) { ThemeStyle.MINT_BREEZE }
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val ordinal = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.ordinal
        ThemeMode.entries.getOrElse(ordinal) { ThemeMode.SYSTEM }
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
        preferences[LARGE_AMOUNT_THRESHOLD_KEY] ?: 0.0
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.ordinal
        }
    }

    suspend fun setThemeStyle(style: ThemeStyle) {
        context.dataStore.edit { preferences ->
            preferences[THEME_STYLE_KEY] = style.ordinal
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
