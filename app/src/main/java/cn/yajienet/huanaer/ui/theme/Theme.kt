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

// Extended color scheme for financial-specific colors
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
    val budgetDangerContainer: Color
)

// CompositionLocal for extended colors
val LocalExtendedColorScheme = staticCompositionLocalOf { MintLightExtendedColorScheme }

// ============================================================
// Default Extended Color Schemes (保留向后兼容)
// ============================================================

private val LightExtendedColorScheme = ExtendedColorScheme(
    income = LightIncome, incomeContainer = LightIncomeContainer, onIncomeContainer = LightOnIncomeContainer,
    expense = LightExpense, expenseContainer = LightExpenseContainer, onExpenseContainer = LightOnExpenseContainer,
    budgetWarning = LightBudgetWarning, budgetWarningContainer = LightBudgetWarningContainer,
    budgetDanger = LightBudgetDanger, budgetDangerContainer = LightBudgetDangerContainer
)

private val DarkExtendedColorScheme = ExtendedColorScheme(
    income = DarkIncome, incomeContainer = DarkIncomeContainer, onIncomeContainer = DarkOnIncomeContainer,
    expense = DarkExpense, expenseContainer = DarkExpenseContainer, onExpenseContainer = DarkOnExpenseContainer,
    budgetWarning = DarkBudgetWarning, budgetWarningContainer = DarkBudgetWarningContainer,
    budgetDanger = DarkBudgetDanger, budgetDangerContainer = DarkBudgetDangerContainer
)

// ============================================================
// Mint Breeze Extended Color Schemes
// ============================================================

private val MintLightExtendedColorScheme = ExtendedColorScheme(
    income = MintLightIncome, incomeContainer = MintLightIncomeContainer, onIncomeContainer = MintLightOnIncomeContainer,
    expense = MintLightExpense, expenseContainer = MintLightExpenseContainer, onExpenseContainer = MintLightOnExpenseContainer,
    budgetWarning = MintLightBudgetWarning, budgetWarningContainer = MintLightBudgetWarningContainer,
    budgetDanger = MintLightBudgetDanger, budgetDangerContainer = MintLightBudgetDangerContainer
)

private val MintDarkExtendedColorScheme = ExtendedColorScheme(
    income = MintDarkIncome, incomeContainer = MintDarkIncomeContainer, onIncomeContainer = MintDarkOnIncomeContainer,
    expense = MintDarkExpense, expenseContainer = MintDarkExpenseContainer, onExpenseContainer = MintDarkOnExpenseContainer,
    budgetWarning = MintDarkBudgetWarning, budgetWarningContainer = MintDarkBudgetWarningContainer,
    budgetDanger = MintDarkBudgetDanger, budgetDangerContainer = MintDarkBudgetDangerContainer
)

// ============================================================
// Sunset Glow Extended Color Schemes
// ============================================================

private val SunsetLightExtendedColorScheme = ExtendedColorScheme(
    income = SunsetLightIncome, incomeContainer = SunsetLightIncomeContainer, onIncomeContainer = SunsetLightOnIncomeContainer,
    expense = SunsetLightExpense, expenseContainer = SunsetLightExpenseContainer, onExpenseContainer = SunsetLightOnExpenseContainer,
    budgetWarning = SunsetLightBudgetWarning, budgetWarningContainer = SunsetLightBudgetWarningContainer,
    budgetDanger = SunsetLightBudgetDanger, budgetDangerContainer = SunsetLightBudgetDangerContainer
)

private val SunsetDarkExtendedColorScheme = ExtendedColorScheme(
    income = SunsetDarkIncome, incomeContainer = SunsetDarkIncomeContainer, onIncomeContainer = SunsetDarkOnIncomeContainer,
    expense = SunsetDarkExpense, expenseContainer = SunsetDarkExpenseContainer, onExpenseContainer = SunsetDarkOnExpenseContainer,
    budgetWarning = SunsetDarkBudgetWarning, budgetWarningContainer = SunsetDarkBudgetWarningContainer,
    budgetDanger = SunsetDarkBudgetDanger, budgetDangerContainer = SunsetDarkBudgetDangerContainer
)

// ============================================================
// Midnight Neon Extended Color Schemes
// ============================================================

private val NeonLightExtendedColorScheme = ExtendedColorScheme(
    income = NeonLightIncome, incomeContainer = NeonLightIncomeContainer, onIncomeContainer = NeonLightOnIncomeContainer,
    expense = NeonLightExpense, expenseContainer = NeonLightExpenseContainer, onExpenseContainer = NeonLightOnExpenseContainer,
    budgetWarning = NeonLightBudgetWarning, budgetWarningContainer = NeonLightBudgetWarningContainer,
    budgetDanger = NeonLightBudgetDanger, budgetDangerContainer = NeonLightBudgetDangerContainer
)

private val NeonDarkExtendedColorScheme = ExtendedColorScheme(
    income = NeonDarkIncome, incomeContainer = NeonDarkIncomeContainer, onIncomeContainer = NeonDarkOnIncomeContainer,
    expense = NeonDarkExpense, expenseContainer = NeonDarkExpenseContainer, onExpenseContainer = NeonDarkOnExpenseContainer,
    budgetWarning = NeonDarkBudgetWarning, budgetWarningContainer = NeonDarkBudgetWarningContainer,
    budgetDanger = NeonDarkBudgetDanger, budgetDangerContainer = NeonDarkBudgetDangerContainer
)

// ============================================================
// Default Color Schemes (保留向后兼容)
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
// Mint Breeze Color Schemes
// ============================================================

private val MintLightColorScheme = lightColorScheme(
    primary = MintLightPrimary, onPrimary = MintLightOnPrimary, primaryContainer = MintLightPrimaryContainer, onPrimaryContainer = MintLightOnPrimaryContainer,
    secondary = MintLightSecondary, onSecondary = MintLightOnSecondary, secondaryContainer = MintLightSecondaryContainer, onSecondaryContainer = MintLightOnSecondaryContainer,
    tertiary = MintLightTertiary, onTertiary = MintLightOnTertiary, tertiaryContainer = MintLightTertiaryContainer, onTertiaryContainer = MintLightOnTertiaryContainer,
    error = MintLightError, onError = MintLightOnError, errorContainer = MintLightErrorContainer, onErrorContainer = MintLightOnErrorContainer,
    background = MintLightBackground, onBackground = MintLightOnBackground, surface = MintLightSurface, onSurface = MintLightOnSurface,
    surfaceVariant = MintLightSurfaceVariant, onSurfaceVariant = MintLightOnSurfaceVariant,
    surfaceContainer = MintLightSurfaceContainer, surfaceContainerHigh = MintLightSurfaceContainerHigh, surfaceContainerHighest = MintLightSurfaceContainerHighest,
    outline = MintLightOutline, outlineVariant = MintLightOutlineVariant,
    inverseSurface = MintLightInverseSurface, inverseOnSurface = MintLightInverseOnSurface, inversePrimary = MintLightInversePrimary,
    surfaceTint = MintLightSurfaceTint, scrim = MintLightScrim
)

private val MintDarkColorScheme = darkColorScheme(
    primary = MintDarkPrimary, onPrimary = MintDarkOnPrimary, primaryContainer = MintDarkPrimaryContainer, onPrimaryContainer = MintDarkOnPrimaryContainer,
    secondary = MintDarkSecondary, onSecondary = MintDarkOnSecondary, secondaryContainer = MintDarkSecondaryContainer, onSecondaryContainer = MintDarkOnSecondaryContainer,
    tertiary = MintDarkTertiary, onTertiary = MintDarkOnTertiary, tertiaryContainer = MintDarkTertiaryContainer, onTertiaryContainer = MintDarkOnTertiaryContainer,
    error = MintDarkError, onError = MintDarkOnError, errorContainer = MintDarkErrorContainer, onErrorContainer = MintDarkOnErrorContainer,
    background = MintDarkBackground, onBackground = MintDarkOnBackground, surface = MintDarkSurface, onSurface = MintDarkOnSurface,
    surfaceVariant = MintDarkSurfaceVariant, onSurfaceVariant = MintDarkOnSurfaceVariant,
    surfaceContainer = MintDarkSurfaceContainer, surfaceContainerHigh = MintDarkSurfaceContainerHigh, surfaceContainerHighest = MintDarkSurfaceContainerHighest,
    outline = MintDarkOutline, outlineVariant = MintDarkOutlineVariant,
    inverseSurface = MintDarkInverseSurface, inverseOnSurface = MintDarkInverseOnSurface, inversePrimary = MintDarkInversePrimary,
    surfaceTint = MintDarkSurfaceTint, scrim = MintDarkScrim
)

// ============================================================
// Sunset Glow Color Schemes
// ============================================================

private val SunsetLightColorScheme = lightColorScheme(
    primary = SunsetLightPrimary, onPrimary = SunsetLightOnPrimary, primaryContainer = SunsetLightPrimaryContainer, onPrimaryContainer = SunsetLightOnPrimaryContainer,
    secondary = SunsetLightSecondary, onSecondary = SunsetLightOnSecondary, secondaryContainer = SunsetLightSecondaryContainer, onSecondaryContainer = SunsetLightOnSecondaryContainer,
    tertiary = SunsetLightTertiary, onTertiary = SunsetLightOnTertiary, tertiaryContainer = SunsetLightTertiaryContainer, onTertiaryContainer = SunsetLightOnTertiaryContainer,
    error = SunsetLightError, onError = SunsetLightOnError, errorContainer = SunsetLightErrorContainer, onErrorContainer = SunsetLightOnErrorContainer,
    background = SunsetLightBackground, onBackground = SunsetLightOnBackground, surface = SunsetLightSurface, onSurface = SunsetLightOnSurface,
    surfaceVariant = SunsetLightSurfaceVariant, onSurfaceVariant = SunsetLightOnSurfaceVariant,
    surfaceContainer = SunsetLightSurfaceContainer, surfaceContainerHigh = SunsetLightSurfaceContainerHigh, surfaceContainerHighest = SunsetLightSurfaceContainerHighest,
    outline = SunsetLightOutline, outlineVariant = SunsetLightOutlineVariant,
    inverseSurface = SunsetLightInverseSurface, inverseOnSurface = SunsetLightInverseOnSurface, inversePrimary = SunsetLightInversePrimary,
    surfaceTint = SunsetLightSurfaceTint, scrim = SunsetLightScrim
)

private val SunsetDarkColorScheme = darkColorScheme(
    primary = SunsetDarkPrimary, onPrimary = SunsetDarkOnPrimary, primaryContainer = SunsetDarkPrimaryContainer, onPrimaryContainer = SunsetDarkOnPrimaryContainer,
    secondary = SunsetDarkSecondary, onSecondary = SunsetDarkOnSecondary, secondaryContainer = SunsetDarkSecondaryContainer, onSecondaryContainer = SunsetDarkOnSecondaryContainer,
    tertiary = SunsetDarkTertiary, onTertiary = SunsetDarkOnTertiary, tertiaryContainer = SunsetDarkTertiaryContainer, onTertiaryContainer = SunsetDarkOnTertiaryContainer,
    error = SunsetDarkError, onError = SunsetDarkOnError, errorContainer = SunsetDarkErrorContainer, onErrorContainer = SunsetDarkOnErrorContainer,
    background = SunsetDarkBackground, onBackground = SunsetDarkOnBackground, surface = SunsetDarkSurface, onSurface = SunsetDarkOnSurface,
    surfaceVariant = SunsetDarkSurfaceVariant, onSurfaceVariant = SunsetDarkOnSurfaceVariant,
    surfaceContainer = SunsetDarkSurfaceContainer, surfaceContainerHigh = SunsetDarkSurfaceContainerHigh, surfaceContainerHighest = SunsetDarkSurfaceContainerHighest,
    outline = SunsetDarkOutline, outlineVariant = SunsetDarkOutlineVariant,
    inverseSurface = SunsetDarkInverseSurface, inverseOnSurface = SunsetDarkInverseOnSurface, inversePrimary = SunsetDarkInversePrimary,
    surfaceTint = SunsetDarkSurfaceTint, scrim = SunsetDarkScrim
)

// ============================================================
// Midnight Neon Color Schemes
// ============================================================

private val NeonLightColorScheme = lightColorScheme(
    primary = NeonLightPrimary, onPrimary = NeonLightOnPrimary, primaryContainer = NeonLightPrimaryContainer, onPrimaryContainer = NeonLightOnPrimaryContainer,
    secondary = NeonLightSecondary, onSecondary = NeonLightOnSecondary, secondaryContainer = NeonLightSecondaryContainer, onSecondaryContainer = NeonLightOnSecondaryContainer,
    tertiary = NeonLightTertiary, onTertiary = NeonLightOnTertiary, tertiaryContainer = NeonLightTertiaryContainer, onTertiaryContainer = NeonLightOnTertiaryContainer,
    error = NeonLightError, onError = NeonLightOnError, errorContainer = NeonLightErrorContainer, onErrorContainer = NeonLightOnErrorContainer,
    background = NeonLightBackground, onBackground = NeonLightOnBackground, surface = NeonLightSurface, onSurface = NeonLightOnSurface,
    surfaceVariant = NeonLightSurfaceVariant, onSurfaceVariant = NeonLightOnSurfaceVariant,
    surfaceContainer = NeonLightSurfaceContainer, surfaceContainerHigh = NeonLightSurfaceContainerHigh, surfaceContainerHighest = NeonLightSurfaceContainerHighest,
    outline = NeonLightOutline, outlineVariant = NeonLightOutlineVariant,
    inverseSurface = NeonLightInverseSurface, inverseOnSurface = NeonLightInverseOnSurface, inversePrimary = NeonLightInversePrimary,
    surfaceTint = NeonLightSurfaceTint, scrim = NeonLightScrim
)

private val NeonDarkColorScheme = darkColorScheme(
    primary = NeonDarkPrimary, onPrimary = NeonDarkOnPrimary, primaryContainer = NeonDarkPrimaryContainer, onPrimaryContainer = NeonDarkOnPrimaryContainer,
    secondary = NeonDarkSecondary, onSecondary = NeonDarkOnSecondary, secondaryContainer = NeonDarkSecondaryContainer, onSecondaryContainer = NeonDarkOnSecondaryContainer,
    tertiary = NeonDarkTertiary, onTertiary = NeonDarkOnTertiary, tertiaryContainer = NeonDarkTertiaryContainer, onTertiaryContainer = NeonDarkOnTertiaryContainer,
    error = NeonDarkError, onError = NeonDarkOnError, errorContainer = NeonDarkErrorContainer, onErrorContainer = NeonDarkOnErrorContainer,
    background = NeonDarkBackground, onBackground = NeonDarkOnBackground, surface = NeonDarkSurface, onSurface = NeonDarkOnSurface,
    surfaceVariant = NeonDarkSurfaceVariant, onSurfaceVariant = NeonDarkOnSurfaceVariant,
    surfaceContainer = NeonDarkSurfaceContainer, surfaceContainerHigh = NeonDarkSurfaceContainerHigh, surfaceContainerHighest = NeonDarkSurfaceContainerHighest,
    outline = NeonDarkOutline, outlineVariant = NeonDarkOutlineVariant,
    inverseSurface = NeonDarkInverseSurface, inverseOnSurface = NeonDarkInverseOnSurface, inversePrimary = NeonDarkInversePrimary,
    surfaceTint = NeonDarkSurfaceTint, scrim = NeonDarkScrim
)

// ============================================================
// Theme Selection Helpers
// ============================================================

private fun selectColorScheme(themeStyle: ThemeStyle, darkTheme: Boolean): ColorScheme {
    return when (themeStyle) {
        ThemeStyle.MINT_BREEZE -> if (darkTheme) MintDarkColorScheme else MintLightColorScheme
        ThemeStyle.SUNSET_GLOW -> if (darkTheme) SunsetDarkColorScheme else SunsetLightColorScheme
        ThemeStyle.MIDNIGHT_NEON -> if (darkTheme) NeonDarkColorScheme else NeonLightColorScheme
    }
}

private fun selectExtendedColorScheme(themeStyle: ThemeStyle, darkTheme: Boolean): ExtendedColorScheme {
    return when (themeStyle) {
        ThemeStyle.MINT_BREEZE -> if (darkTheme) MintDarkExtendedColorScheme else MintLightExtendedColorScheme
        ThemeStyle.SUNSET_GLOW -> if (darkTheme) SunsetDarkExtendedColorScheme else SunsetLightExtendedColorScheme
        ThemeStyle.MIDNIGHT_NEON -> if (darkTheme) NeonDarkExtendedColorScheme else NeonLightExtendedColorScheme
    }
}

// ============================================================
// Main Theme Composable
// ============================================================

@Composable
fun HuaNaErTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themeStyle: ThemeStyle = ThemeStyle.MINT_BREEZE,
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

    CompositionLocalProvider(LocalExtendedColorScheme provides extendedColorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

@Composable
fun extendedColorScheme(): ExtendedColorScheme {
    return LocalExtendedColorScheme.current
}
