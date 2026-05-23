package cn.yajienet.huanaer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import cn.yajienet.huanaer.data.datastore.ThemeStyle

// ============================================================
// Extended Color Scheme — 金融语义色 + 弥散光斑色
// ============================================================

data class ExtendedColorScheme(
    val income: Color,
    val incomeContainer: Color,
    val onIncomeContainer: Color,
    val expense: Color,
    val expenseContainer: Color,
    val onExpenseContainer: Color,
    val budgetWarning: Color,
    val budgetWarningContainer: Color,
    val budgetDanger: Color,
    val budgetDangerContainer: Color,
    // 新增弥散光斑色
    val glowPrimary: Color,
    val glowSecondary: Color,
    val glowAccent: Color
)

val LocalExtendedColorScheme = staticCompositionLocalOf { MintMilkLightExtendedColorScheme }

@Composable
fun extendedColorScheme(): ExtendedColorScheme = LocalExtendedColorScheme.current

// ============================================================
// Default Extended Color Schemes (保留向后兼容 fallback)
// ============================================================

private val LightExtendedColorScheme = ExtendedColorScheme(
    income = LightIncome, incomeContainer = LightIncomeContainer, onIncomeContainer = LightOnIncomeContainer,
    expense = LightExpense, expenseContainer = LightExpenseContainer, onExpenseContainer = LightOnExpenseContainer,
    budgetWarning = LightBudgetWarning, budgetWarningContainer = LightBudgetWarningContainer,
    budgetDanger = LightBudgetDanger, budgetDangerContainer = LightBudgetDangerContainer,
    glowPrimary = Color(0x1A5A67D8), glowSecondary = Color(0x15667EEA), glowAccent = Color(0x154C51BF)
)

private val DarkExtendedColorScheme = ExtendedColorScheme(
    income = DarkIncome, incomeContainer = DarkIncomeContainer, onIncomeContainer = DarkOnIncomeContainer,
    expense = DarkExpense, expenseContainer = DarkExpenseContainer, onExpenseContainer = DarkOnExpenseContainer,
    budgetWarning = DarkBudgetWarning, budgetWarningContainer = DarkBudgetWarningContainer,
    budgetDanger = DarkBudgetDanger, budgetDangerContainer = DarkBudgetDangerContainer,
    glowPrimary = Color(0x0DB8C1F5), glowSecondary = Color(0x08A0B0E8), glowAccent = Color(0x089AA8E0)
)

// ============================================================
// 薄荷奶绿 Extended Color Schemes (MintMilk)
// ============================================================

private val MintMilkLightExtendedColorScheme = ExtendedColorScheme(
    income = MintMilkLightIncome, incomeContainer = MintMilkLightIncomeContainer, onIncomeContainer = MintMilkLightOnIncomeContainer,
    expense = MintMilkLightExpense, expenseContainer = MintMilkLightExpenseContainer, onExpenseContainer = MintMilkLightOnExpenseContainer,
    budgetWarning = MintMilkLightBudgetWarning, budgetWarningContainer = MintMilkLightBudgetWarningContainer,
    budgetDanger = MintMilkLightBudgetDanger, budgetDangerContainer = MintMilkLightBudgetDangerContainer,
    glowPrimary = MintMilkLightGlowPrimary, glowSecondary = MintMilkLightGlowSecondary, glowAccent = MintMilkLightGlowAccent
)

private val MintMilkDarkExtendedColorScheme = ExtendedColorScheme(
    income = MintMilkDarkIncome, incomeContainer = MintMilkDarkIncomeContainer, onIncomeContainer = MintMilkDarkOnIncomeContainer,
    expense = MintMilkDarkExpense, expenseContainer = MintMilkDarkExpenseContainer, onExpenseContainer = MintMilkDarkOnExpenseContainer,
    budgetWarning = MintMilkDarkBudgetWarning, budgetWarningContainer = MintMilkDarkBudgetWarningContainer,
    budgetDanger = MintMilkDarkBudgetDanger, budgetDangerContainer = MintMilkDarkBudgetDangerContainer,
    glowPrimary = MintMilkDarkGlowPrimary, glowSecondary = MintMilkDarkGlowSecondary, glowAccent = MintMilkDarkGlowAccent
)

// ============================================================
// 薰衣草紫 Extended Color Schemes (Lavender)
// ============================================================

private val LavenderLightExtendedColorScheme = ExtendedColorScheme(
    income = LavenderLightIncome, incomeContainer = LavenderLightIncomeContainer, onIncomeContainer = LavenderLightOnIncomeContainer,
    expense = LavenderLightExpense, expenseContainer = LavenderLightExpenseContainer, onExpenseContainer = LavenderLightOnExpenseContainer,
    budgetWarning = LavenderLightBudgetWarning, budgetWarningContainer = LavenderLightBudgetWarningContainer,
    budgetDanger = LavenderLightBudgetDanger, budgetDangerContainer = LavenderLightBudgetDangerContainer,
    glowPrimary = LavenderLightGlowPrimary, glowSecondary = LavenderLightGlowSecondary, glowAccent = LavenderLightGlowAccent
)

private val LavenderDarkExtendedColorScheme = ExtendedColorScheme(
    income = LavenderDarkIncome, incomeContainer = LavenderDarkIncomeContainer, onIncomeContainer = LavenderDarkOnIncomeContainer,
    expense = LavenderDarkExpense, expenseContainer = LavenderDarkExpenseContainer, onExpenseContainer = LavenderDarkOnExpenseContainer,
    budgetWarning = LavenderDarkBudgetWarning, budgetWarningContainer = LavenderDarkBudgetWarningContainer,
    budgetDanger = LavenderDarkBudgetDanger, budgetDangerContainer = LavenderDarkBudgetDangerContainer,
    glowPrimary = LavenderDarkGlowPrimary, glowSecondary = LavenderDarkGlowSecondary, glowAccent = LavenderDarkGlowAccent
)

// ============================================================
// 暖阳蜜桃 Extended Color Schemes (WarmPeach)
// ============================================================

private val WarmPeachLightExtendedColorScheme = ExtendedColorScheme(
    income = WarmPeachLightIncome, incomeContainer = WarmPeachLightIncomeContainer, onIncomeContainer = WarmPeachLightOnIncomeContainer,
    expense = WarmPeachLightExpense, expenseContainer = WarmPeachLightExpenseContainer, onExpenseContainer = WarmPeachLightOnExpenseContainer,
    budgetWarning = WarmPeachLightBudgetWarning, budgetWarningContainer = WarmPeachLightBudgetWarningContainer,
    budgetDanger = WarmPeachLightBudgetDanger, budgetDangerContainer = WarmPeachLightBudgetDangerContainer,
    glowPrimary = WarmPeachLightGlowPrimary, glowSecondary = WarmPeachLightGlowSecondary, glowAccent = WarmPeachLightGlowAccent
)

private val WarmPeachDarkExtendedColorScheme = ExtendedColorScheme(
    income = WarmPeachDarkIncome, incomeContainer = WarmPeachDarkIncomeContainer, onIncomeContainer = WarmPeachDarkOnIncomeContainer,
    expense = WarmPeachDarkExpense, expenseContainer = WarmPeachDarkExpenseContainer, onExpenseContainer = WarmPeachDarkOnExpenseContainer,
    budgetWarning = WarmPeachDarkBudgetWarning, budgetWarningContainer = WarmPeachDarkBudgetWarningContainer,
    budgetDanger = WarmPeachDarkBudgetDanger, budgetDangerContainer = WarmPeachDarkBudgetDangerContainer,
    glowPrimary = WarmPeachDarkGlowPrimary, glowSecondary = WarmPeachDarkGlowSecondary, glowAccent = WarmPeachDarkGlowAccent
)

// ============================================================
// Default Color Schemes (保留向后兼容 fallback)
// ============================================================

private val DefaultLightColorScheme = lightColorScheme(
    primary = LightPrimary, onPrimary = LightOnPrimary, primaryContainer = LightPrimaryContainer, onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary, onSecondary = LightOnSecondary, secondaryContainer = LightSecondaryContainer, onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary, onTertiary = LightOnTertiary, tertiaryContainer = LightTertiaryContainer, onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError, onError = LightOnError, errorContainer = LightErrorContainer, onErrorContainer = LightOnErrorContainer,
    background = LightBackground, onBackground = LightOnBackground, surface = LightSurface, onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant, onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainer = LightSurfaceContainer, surfaceContainerHigh = LightSurfaceContainerHigh, surfaceContainerHighest = LightSurfaceContainerHighest,
    outline = LightOutline, outlineVariant = LightOutlineVariant,
    inverseSurface = LightInverseSurface, inverseOnSurface = LightInverseOnSurface, inversePrimary = LightInversePrimary,
    surfaceTint = LightSurfaceTint, scrim = LightScrim
)

private val DefaultDarkColorScheme = darkColorScheme(
    primary = DarkPrimary, onPrimary = DarkOnPrimary, primaryContainer = DarkPrimaryContainer, onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary, onSecondary = DarkOnSecondary, secondaryContainer = DarkSecondaryContainer, onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary, onTertiary = DarkOnTertiary, tertiaryContainer = DarkTertiaryContainer, onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError, onError = DarkOnError, errorContainer = DarkErrorContainer, onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground, onBackground = DarkOnBackground, surface = DarkSurface, onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant, onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainer = DarkSurfaceContainer, surfaceContainerHigh = DarkSurfaceContainerHigh, surfaceContainerHighest = DarkSurfaceContainerHighest,
    outline = DarkOutline, outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkInverseSurface, inverseOnSurface = DarkInverseOnSurface, inversePrimary = DarkInversePrimary,
    surfaceTint = DarkSurfaceTint, scrim = DarkScrim
)

// ============================================================
// 薄荷奶绿 Color Schemes (MintMilk)
// ============================================================

private val MintMilkLightColorScheme = lightColorScheme(
    primary = MintMilkLightPrimary, onPrimary = MintMilkLightOnPrimary, primaryContainer = MintMilkLightPrimaryContainer, onPrimaryContainer = MintMilkLightOnPrimaryContainer,
    secondary = MintMilkLightSecondary, onSecondary = MintMilkLightOnSecondary, secondaryContainer = MintMilkLightSecondaryContainer, onSecondaryContainer = MintMilkLightOnSecondaryContainer,
    tertiary = MintMilkLightTertiary, onTertiary = MintMilkLightOnTertiary, tertiaryContainer = MintMilkLightTertiaryContainer, onTertiaryContainer = MintMilkLightOnTertiaryContainer,
    error = MintMilkLightError, onError = MintMilkLightOnError, errorContainer = MintMilkLightErrorContainer, onErrorContainer = MintMilkLightOnErrorContainer,
    background = MintMilkLightBackground, onBackground = MintMilkLightOnBackground, surface = MintMilkLightSurface, onSurface = MintMilkLightOnSurface,
    surfaceVariant = MintMilkLightSurfaceVariant, onSurfaceVariant = MintMilkLightOnSurfaceVariant,
    surfaceContainer = MintMilkLightSurfaceContainer, surfaceContainerHigh = MintMilkLightSurfaceContainerHigh, surfaceContainerHighest = MintMilkLightSurfaceContainerHighest,
    outline = MintMilkLightOutline, outlineVariant = MintMilkLightOutlineVariant,
    inverseSurface = MintMilkLightInverseSurface, inverseOnSurface = MintMilkLightInverseOnSurface, inversePrimary = MintMilkLightInversePrimary,
    surfaceTint = MintMilkLightSurfaceTint, scrim = MintMilkLightScrim
)

private val MintMilkDarkColorScheme = darkColorScheme(
    primary = MintMilkDarkPrimary, onPrimary = MintMilkDarkOnPrimary, primaryContainer = MintMilkDarkPrimaryContainer, onPrimaryContainer = MintMilkDarkOnPrimaryContainer,
    secondary = MintMilkDarkSecondary, onSecondary = MintMilkDarkOnSecondary, secondaryContainer = MintMilkDarkSecondaryContainer, onSecondaryContainer = MintMilkDarkOnSecondaryContainer,
    tertiary = MintMilkDarkTertiary, onTertiary = MintMilkDarkOnTertiary, tertiaryContainer = MintMilkDarkTertiaryContainer, onTertiaryContainer = MintMilkDarkOnTertiaryContainer,
    error = MintMilkDarkError, onError = MintMilkDarkOnError, errorContainer = MintMilkDarkErrorContainer, onErrorContainer = MintMilkDarkOnErrorContainer,
    background = MintMilkDarkBackground, onBackground = MintMilkDarkOnBackground, surface = MintMilkDarkSurface, onSurface = MintMilkDarkOnSurface,
    surfaceVariant = MintMilkDarkSurfaceVariant, onSurfaceVariant = MintMilkDarkOnSurfaceVariant,
    surfaceContainer = MintMilkDarkSurfaceContainer, surfaceContainerHigh = MintMilkDarkSurfaceContainerHigh, surfaceContainerHighest = MintMilkDarkSurfaceContainerHighest,
    outline = MintMilkDarkOutline, outlineVariant = MintMilkDarkOutlineVariant,
    inverseSurface = MintMilkDarkInverseSurface, inverseOnSurface = MintMilkDarkInverseOnSurface, inversePrimary = MintMilkDarkInversePrimary,
    surfaceTint = MintMilkDarkSurfaceTint, scrim = MintMilkDarkScrim
)

// ============================================================
// 薰衣草紫 Color Schemes (Lavender)
// ============================================================

private val LavenderLightColorScheme = lightColorScheme(
    primary = LavenderLightPrimary, onPrimary = LavenderLightOnPrimary, primaryContainer = LavenderLightPrimaryContainer, onPrimaryContainer = LavenderLightOnPrimaryContainer,
    secondary = LavenderLightSecondary, onSecondary = LavenderLightOnSecondary, secondaryContainer = LavenderLightSecondaryContainer, onSecondaryContainer = LavenderLightOnSecondaryContainer,
    tertiary = LavenderLightTertiary, onTertiary = LavenderLightOnTertiary, tertiaryContainer = LavenderLightTertiaryContainer, onTertiaryContainer = LavenderLightOnTertiaryContainer,
    error = LavenderLightError, onError = LavenderLightOnError, errorContainer = LavenderLightErrorContainer, onErrorContainer = LavenderLightOnErrorContainer,
    background = LavenderLightBackground, onBackground = LavenderLightOnBackground, surface = LavenderLightSurface, onSurface = LavenderLightOnSurface,
    surfaceVariant = LavenderLightSurfaceVariant, onSurfaceVariant = LavenderLightOnSurfaceVariant,
    surfaceContainer = LavenderLightSurfaceContainer, surfaceContainerHigh = LavenderLightSurfaceContainerHigh, surfaceContainerHighest = LavenderLightSurfaceContainerHighest,
    outline = LavenderLightOutline, outlineVariant = LavenderLightOutlineVariant,
    inverseSurface = LavenderLightInverseSurface, inverseOnSurface = LavenderLightInverseOnSurface, inversePrimary = LavenderLightInversePrimary,
    surfaceTint = LavenderLightSurfaceTint, scrim = LavenderLightScrim
)

private val LavenderDarkColorScheme = darkColorScheme(
    primary = LavenderDarkPrimary, onPrimary = LavenderDarkOnPrimary, primaryContainer = LavenderDarkPrimaryContainer, onPrimaryContainer = LavenderDarkOnPrimaryContainer,
    secondary = LavenderDarkSecondary, onSecondary = LavenderDarkOnSecondary, secondaryContainer = LavenderDarkSecondaryContainer, onSecondaryContainer = LavenderDarkOnSecondaryContainer,
    tertiary = LavenderDarkTertiary, onTertiary = LavenderDarkOnTertiary, tertiaryContainer = LavenderDarkTertiaryContainer, onTertiaryContainer = LavenderDarkOnTertiaryContainer,
    error = LavenderDarkError, onError = LavenderDarkOnError, errorContainer = LavenderDarkErrorContainer, onErrorContainer = LavenderDarkOnErrorContainer,
    background = LavenderDarkBackground, onBackground = LavenderDarkOnBackground, surface = LavenderDarkSurface, onSurface = LavenderDarkOnSurface,
    surfaceVariant = LavenderDarkSurfaceVariant, onSurfaceVariant = LavenderDarkOnSurfaceVariant,
    surfaceContainer = LavenderDarkSurfaceContainer, surfaceContainerHigh = LavenderDarkSurfaceContainerHigh, surfaceContainerHighest = LavenderDarkSurfaceContainerHighest,
    outline = LavenderDarkOutline, outlineVariant = LavenderDarkOutlineVariant,
    inverseSurface = LavenderDarkInverseSurface, inverseOnSurface = LavenderDarkInverseOnSurface, inversePrimary = LavenderDarkInversePrimary,
    surfaceTint = LavenderDarkSurfaceTint, scrim = LavenderDarkScrim
)

// ============================================================
// 暖阳蜜桃 Color Schemes (WarmPeach)
// ============================================================

private val WarmPeachLightColorScheme = lightColorScheme(
    primary = WarmPeachLightPrimary, onPrimary = WarmPeachLightOnPrimary, primaryContainer = WarmPeachLightPrimaryContainer, onPrimaryContainer = WarmPeachLightOnPrimaryContainer,
    secondary = WarmPeachLightSecondary, onSecondary = WarmPeachLightOnSecondary, secondaryContainer = WarmPeachLightSecondaryContainer, onSecondaryContainer = WarmPeachLightOnSecondaryContainer,
    tertiary = WarmPeachLightTertiary, onTertiary = WarmPeachLightOnTertiary, tertiaryContainer = WarmPeachLightTertiaryContainer, onTertiaryContainer = WarmPeachLightOnTertiaryContainer,
    error = WarmPeachLightError, onError = WarmPeachLightOnError, errorContainer = WarmPeachLightErrorContainer, onErrorContainer = WarmPeachLightOnErrorContainer,
    background = WarmPeachLightBackground, onBackground = WarmPeachLightOnBackground, surface = WarmPeachLightSurface, onSurface = WarmPeachLightOnSurface,
    surfaceVariant = WarmPeachLightSurfaceVariant, onSurfaceVariant = WarmPeachLightOnSurfaceVariant,
    surfaceContainer = WarmPeachLightSurfaceContainer, surfaceContainerHigh = WarmPeachLightSurfaceContainerHigh, surfaceContainerHighest = WarmPeachLightSurfaceContainerHighest,
    outline = WarmPeachLightOutline, outlineVariant = WarmPeachLightOutlineVariant,
    inverseSurface = WarmPeachLightInverseSurface, inverseOnSurface = WarmPeachLightInverseOnSurface, inversePrimary = WarmPeachLightInversePrimary,
    surfaceTint = WarmPeachLightSurfaceTint, scrim = WarmPeachLightScrim
)

private val WarmPeachDarkColorScheme = darkColorScheme(
    primary = WarmPeachDarkPrimary, onPrimary = WarmPeachDarkOnPrimary, primaryContainer = WarmPeachDarkPrimaryContainer, onPrimaryContainer = WarmPeachDarkOnPrimaryContainer,
    secondary = WarmPeachDarkSecondary, onSecondary = WarmPeachDarkOnSecondary, secondaryContainer = WarmPeachDarkSecondaryContainer, onSecondaryContainer = WarmPeachDarkOnSecondaryContainer,
    tertiary = WarmPeachDarkTertiary, onTertiary = WarmPeachDarkOnTertiary, tertiaryContainer = WarmPeachDarkTertiaryContainer, onTertiaryContainer = WarmPeachDarkOnTertiaryContainer,
    error = WarmPeachDarkError, onError = WarmPeachDarkOnError, errorContainer = WarmPeachDarkErrorContainer, onErrorContainer = WarmPeachDarkOnErrorContainer,
    background = WarmPeachDarkBackground, onBackground = WarmPeachDarkOnBackground, surface = WarmPeachDarkSurface, onSurface = WarmPeachDarkOnSurface,
    surfaceVariant = WarmPeachDarkSurfaceVariant, onSurfaceVariant = WarmPeachDarkOnSurfaceVariant,
    surfaceContainer = WarmPeachDarkSurfaceContainer, surfaceContainerHigh = WarmPeachDarkSurfaceContainerHigh, surfaceContainerHighest = WarmPeachDarkSurfaceContainerHighest,
    outline = WarmPeachDarkOutline, outlineVariant = WarmPeachDarkOutlineVariant,
    inverseSurface = WarmPeachDarkInverseSurface, inverseOnSurface = WarmPeachDarkInverseOnSurface, inversePrimary = WarmPeachDarkInversePrimary,
    surfaceTint = WarmPeachDarkSurfaceTint, scrim = WarmPeachDarkScrim
)

// ============================================================
// Theme Selection Helpers — 使用新的枚举值
// ============================================================

private fun selectColorScheme(themeStyle: ThemeStyle, darkTheme: Boolean): ColorScheme {
    return when (themeStyle) {
        ThemeStyle.MINT_MILK -> if (darkTheme) MintMilkDarkColorScheme else MintMilkLightColorScheme
        ThemeStyle.LAVENDER -> if (darkTheme) LavenderDarkColorScheme else LavenderLightColorScheme
        ThemeStyle.WARM_PEACH -> if (darkTheme) WarmPeachDarkColorScheme else WarmPeachLightColorScheme
        // 向后兼容：旧枚举值映射到新色系
        ThemeStyle.MINT_BREEZE -> if (darkTheme) MintMilkDarkColorScheme else MintMilkLightColorScheme
        ThemeStyle.SUNSET_GLOW -> if (darkTheme) LavenderDarkColorScheme else LavenderLightColorScheme
        ThemeStyle.MIDNIGHT_NEON -> if (darkTheme) WarmPeachDarkColorScheme else WarmPeachLightColorScheme
    }
}

private fun selectExtendedColorScheme(themeStyle: ThemeStyle, darkTheme: Boolean): ExtendedColorScheme {
    return when (themeStyle) {
        ThemeStyle.MINT_MILK -> if (darkTheme) MintMilkDarkExtendedColorScheme else MintMilkLightExtendedColorScheme
        ThemeStyle.LAVENDER -> if (darkTheme) LavenderDarkExtendedColorScheme else LavenderLightExtendedColorScheme
        ThemeStyle.WARM_PEACH -> if (darkTheme) WarmPeachDarkExtendedColorScheme else WarmPeachLightExtendedColorScheme
        // 向后兼容：旧枚举值映射到新色系
        ThemeStyle.MINT_BREEZE -> if (darkTheme) MintMilkDarkExtendedColorScheme else MintMilkLightExtendedColorScheme
        ThemeStyle.SUNSET_GLOW -> if (darkTheme) LavenderDarkExtendedColorScheme else LavenderLightExtendedColorScheme
        ThemeStyle.MIDNIGHT_NEON -> if (darkTheme) WarmPeachDarkExtendedColorScheme else WarmPeachLightExtendedColorScheme
    }
}

// ============================================================
// Main Theme Composable — 注入 Shape 和 AnimationSpecs
// ============================================================

@Composable
fun HuaNaErTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themeStyle: ThemeStyle = ThemeStyle.MINT_MILK,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> selectColorScheme(themeStyle, darkTheme)
    }

    val extendedColorScheme = selectExtendedColorScheme(themeStyle, darkTheme)

    CompositionLocalProvider(
        LocalExtendedColorScheme provides extendedColorScheme,
        LocalAppShapes provides AppShapes(),
        LocalAnimationSpecs provides AnimationSpecs()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}