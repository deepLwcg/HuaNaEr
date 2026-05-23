package cn.yajienet.huanaer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import cn.yajienet.huanaer.data.datastore.ThemeStyle

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
    val glowPrimary: Color,
    val glowSecondary: Color,
    val glowAccent: Color
)

val LocalExtendedColorScheme = staticCompositionLocalOf { StrawberryShakeLightExtendedColorScheme }

@Composable
fun extendedColorScheme(): ExtendedColorScheme = LocalExtendedColorScheme.current

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

private val StrawberryShakeLightExtendedColorScheme = ExtendedColorScheme(
    income = StrawberryShakeLightIncome, incomeContainer = StrawberryShakeLightIncomeContainer,
    onIncomeContainer = StrawberryShakeLightOnIncomeContainer,
    expense = StrawberryShakeLightExpense, expenseContainer = StrawberryShakeLightExpenseContainer,
    onExpenseContainer = StrawberryShakeLightOnExpenseContainer,
    budgetWarning = StrawberryShakeLightBudgetWarning, budgetWarningContainer = StrawberryShakeLightBudgetWarningContainer,
    budgetDanger = StrawberryShakeLightBudgetDanger, budgetDangerContainer = StrawberryShakeLightBudgetDangerContainer,
    glowPrimary = StrawberryShakeLightGlowPrimary, glowSecondary = StrawberryShakeLightGlowSecondary,
    glowAccent = StrawberryShakeLightGlowAccent
)

private val StrawberryShakeDarkExtendedColorScheme = ExtendedColorScheme(
    income = StrawberryShakeDarkIncome, incomeContainer = StrawberryShakeDarkIncomeContainer,
    onIncomeContainer = StrawberryShakeDarkOnIncomeContainer,
    expense = StrawberryShakeDarkExpense, expenseContainer = StrawberryShakeDarkExpenseContainer,
    onExpenseContainer = StrawberryShakeDarkOnExpenseContainer,
    budgetWarning = StrawberryShakeDarkBudgetWarning, budgetWarningContainer = StrawberryShakeDarkBudgetWarningContainer,
    budgetDanger = StrawberryShakeDarkBudgetDanger, budgetDangerContainer = StrawberryShakeDarkBudgetDangerContainer,
    glowPrimary = StrawberryShakeDarkGlowPrimary, glowSecondary = StrawberryShakeDarkGlowSecondary,
    glowAccent = StrawberryShakeDarkGlowAccent
)

private val SeaSaltSodaLightExtendedColorScheme = ExtendedColorScheme(
    income = SeaSaltSodaLightIncome, incomeContainer = SeaSaltSodaLightIncomeContainer,
    onIncomeContainer = SeaSaltSodaLightOnIncomeContainer,
    expense = SeaSaltSodaLightExpense, expenseContainer = SeaSaltSodaLightExpenseContainer,
    onExpenseContainer = SeaSaltSodaLightOnExpenseContainer,
    budgetWarning = SeaSaltSodaLightBudgetWarning, budgetWarningContainer = SeaSaltSodaLightBudgetWarningContainer,
    budgetDanger = SeaSaltSodaLightBudgetDanger, budgetDangerContainer = SeaSaltSodaLightBudgetDangerContainer,
    glowPrimary = SeaSaltSodaLightGlowPrimary, glowSecondary = SeaSaltSodaLightGlowSecondary,
    glowAccent = SeaSaltSodaLightGlowAccent
)

private val SeaSaltSodaDarkExtendedColorScheme = ExtendedColorScheme(
    income = SeaSaltSodaDarkIncome, incomeContainer = SeaSaltSodaDarkIncomeContainer,
    onIncomeContainer = SeaSaltSodaDarkOnIncomeContainer,
    expense = SeaSaltSodaDarkExpense, expenseContainer = SeaSaltSodaDarkExpenseContainer,
    onExpenseContainer = SeaSaltSodaDarkOnExpenseContainer,
    budgetWarning = SeaSaltSodaDarkBudgetWarning, budgetWarningContainer = SeaSaltSodaDarkBudgetWarningContainer,
    budgetDanger = SeaSaltSodaDarkBudgetDanger, budgetDangerContainer = SeaSaltSodaDarkBudgetDangerContainer,
    glowPrimary = SeaSaltSodaDarkGlowPrimary, glowSecondary = SeaSaltSodaDarkGlowSecondary,
    glowAccent = SeaSaltSodaDarkGlowAccent
)

private val GrapeBubbleLightExtendedColorScheme = ExtendedColorScheme(
    income = GrapeBubbleLightIncome, incomeContainer = GrapeBubbleLightIncomeContainer,
    onIncomeContainer = GrapeBubbleLightOnIncomeContainer,
    expense = GrapeBubbleLightExpense, expenseContainer = GrapeBubbleLightExpenseContainer,
    onExpenseContainer = GrapeBubbleLightOnExpenseContainer,
    budgetWarning = GrapeBubbleLightBudgetWarning, budgetWarningContainer = GrapeBubbleLightBudgetWarningContainer,
    budgetDanger = GrapeBubbleLightBudgetDanger, budgetDangerContainer = GrapeBubbleLightBudgetDangerContainer,
    glowPrimary = GrapeBubbleLightGlowPrimary, glowSecondary = GrapeBubbleLightGlowSecondary,
    glowAccent = GrapeBubbleLightGlowAccent
)

private val GrapeBubbleDarkExtendedColorScheme = ExtendedColorScheme(
    income = GrapeBubbleDarkIncome, incomeContainer = GrapeBubbleDarkIncomeContainer,
    onIncomeContainer = GrapeBubbleDarkOnIncomeContainer,
    expense = GrapeBubbleDarkExpense, expenseContainer = GrapeBubbleDarkExpenseContainer,
    onExpenseContainer = GrapeBubbleDarkOnExpenseContainer,
    budgetWarning = GrapeBubbleDarkBudgetWarning, budgetWarningContainer = GrapeBubbleDarkBudgetWarningContainer,
    budgetDanger = GrapeBubbleDarkBudgetDanger, budgetDangerContainer = GrapeBubbleDarkBudgetDangerContainer,
    glowPrimary = GrapeBubbleDarkGlowPrimary, glowSecondary = GrapeBubbleDarkGlowSecondary,
    glowAccent = GrapeBubbleDarkGlowAccent
)

private val DefaultLightColorScheme = lightColorScheme(
    primary = LightPrimary, onPrimary = LightOnPrimary, primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer, secondary = LightSecondary, onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer, onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary, onTertiary = LightOnTertiary, tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer, error = LightError, onError = LightOnError,
    errorContainer = LightErrorContainer, onErrorContainer = LightOnErrorContainer,
    background = LightBackground, onBackground = LightOnBackground, surface = LightSurface,
    onSurface = LightOnSurface, surfaceVariant = LightSurfaceVariant, onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainer = LightSurfaceContainer, surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest, outline = LightOutline,
    outlineVariant = LightOutlineVariant, inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface, inversePrimary = LightInversePrimary,
    surfaceTint = LightSurfaceTint, scrim = LightScrim
)

private val DefaultDarkColorScheme = darkColorScheme(
    primary = DarkPrimary, onPrimary = DarkOnPrimary, primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer, secondary = DarkSecondary, onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer, onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary, onTertiary = DarkOnTertiary, tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer, error = DarkError, onError = DarkOnError,
    errorContainer = DarkErrorContainer, onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground, onBackground = DarkOnBackground, surface = DarkSurface,
    onSurface = DarkOnSurface, surfaceVariant = DarkSurfaceVariant, onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainer = DarkSurfaceContainer, surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest, outline = DarkOutline,
    outlineVariant = DarkOutlineVariant, inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface, inversePrimary = DarkInversePrimary,
    surfaceTint = DarkSurfaceTint, scrim = DarkScrim
)

private val StrawberryShakeLightColorScheme = lightColorScheme(
    primary = StrawberryShakeLightPrimary, onPrimary = StrawberryShakeLightOnPrimary,
    primaryContainer = StrawberryShakeLightPrimaryContainer, onPrimaryContainer = StrawberryShakeLightOnPrimaryContainer,
    secondary = StrawberryShakeLightSecondary, onSecondary = StrawberryShakeLightOnSecondary,
    secondaryContainer = StrawberryShakeLightSecondaryContainer, onSecondaryContainer = StrawberryShakeLightOnSecondaryContainer,
    tertiary = StrawberryShakeLightTertiary, onTertiary = StrawberryShakeLightOnTertiary,
    tertiaryContainer = StrawberryShakeLightTertiaryContainer, onTertiaryContainer = StrawberryShakeLightOnTertiaryContainer,
    error = StrawberryShakeLightError, onError = StrawberryShakeLightOnError,
    errorContainer = StrawberryShakeLightErrorContainer, onErrorContainer = StrawberryShakeLightOnErrorContainer,
    background = StrawberryShakeLightBackground, onBackground = StrawberryShakeLightOnBackground,
    surface = StrawberryShakeLightSurface, onSurface = StrawberryShakeLightOnSurface,
    surfaceVariant = StrawberryShakeLightSurfaceVariant, onSurfaceVariant = StrawberryShakeLightOnSurfaceVariant,
    surfaceContainer = StrawberryShakeLightSurfaceContainer, surfaceContainerHigh = StrawberryShakeLightSurfaceContainerHigh,
    surfaceContainerHighest = StrawberryShakeLightSurfaceContainerHighest, outline = StrawberryShakeLightOutline,
    outlineVariant = StrawberryShakeLightOutlineVariant, inverseSurface = StrawberryShakeLightInverseSurface,
    inverseOnSurface = StrawberryShakeLightInverseOnSurface, inversePrimary = StrawberryShakeLightInversePrimary,
    surfaceTint = StrawberryShakeLightSurfaceTint, scrim = StrawberryShakeLightScrim
)

private val StrawberryShakeDarkColorScheme = darkColorScheme(
    primary = StrawberryShakeDarkPrimary, onPrimary = StrawberryShakeDarkOnPrimary,
    primaryContainer = StrawberryShakeDarkPrimaryContainer, onPrimaryContainer = StrawberryShakeDarkOnPrimaryContainer,
    secondary = StrawberryShakeDarkSecondary, onSecondary = StrawberryShakeDarkOnSecondary,
    secondaryContainer = StrawberryShakeDarkSecondaryContainer, onSecondaryContainer = StrawberryShakeDarkOnSecondaryContainer,
    tertiary = StrawberryShakeDarkTertiary, onTertiary = StrawberryShakeDarkOnTertiary,
    tertiaryContainer = StrawberryShakeDarkTertiaryContainer, onTertiaryContainer = StrawberryShakeDarkOnTertiaryContainer,
    error = StrawberryShakeDarkError, onError = StrawberryShakeDarkOnError,
    errorContainer = StrawberryShakeDarkErrorContainer, onErrorContainer = StrawberryShakeDarkOnErrorContainer,
    background = StrawberryShakeDarkBackground, onBackground = StrawberryShakeDarkOnBackground,
    surface = StrawberryShakeDarkSurface, onSurface = StrawberryShakeDarkOnSurface,
    surfaceVariant = StrawberryShakeDarkSurfaceVariant, onSurfaceVariant = StrawberryShakeDarkOnSurfaceVariant,
    surfaceContainer = StrawberryShakeDarkSurfaceContainer, surfaceContainerHigh = StrawberryShakeDarkSurfaceContainerHigh,
    surfaceContainerHighest = StrawberryShakeDarkSurfaceContainerHighest, outline = StrawberryShakeDarkOutline,
    outlineVariant = StrawberryShakeDarkOutlineVariant, inverseSurface = StrawberryShakeDarkInverseSurface,
    inverseOnSurface = StrawberryShakeDarkInverseOnSurface, inversePrimary = StrawberryShakeDarkInversePrimary,
    surfaceTint = StrawberryShakeDarkSurfaceTint, scrim = StrawberryShakeDarkScrim
)

private val SeaSaltSodaLightColorScheme = lightColorScheme(
    primary = SeaSaltSodaLightPrimary, onPrimary = SeaSaltSodaLightOnPrimary,
    primaryContainer = SeaSaltSodaLightPrimaryContainer, onPrimaryContainer = SeaSaltSodaLightOnPrimaryContainer,
    secondary = SeaSaltSodaLightSecondary, onSecondary = SeaSaltSodaLightOnSecondary,
    secondaryContainer = SeaSaltSodaLightSecondaryContainer, onSecondaryContainer = SeaSaltSodaLightOnSecondaryContainer,
    tertiary = SeaSaltSodaLightTertiary, onTertiary = SeaSaltSodaLightOnTertiary,
    tertiaryContainer = SeaSaltSodaLightTertiaryContainer, onTertiaryContainer = SeaSaltSodaLightOnTertiaryContainer,
    error = SeaSaltSodaLightError, onError = SeaSaltSodaLightOnError,
    errorContainer = SeaSaltSodaLightErrorContainer, onErrorContainer = SeaSaltSodaLightOnErrorContainer,
    background = SeaSaltSodaLightBackground, onBackground = SeaSaltSodaLightOnBackground,
    surface = SeaSaltSodaLightSurface, onSurface = SeaSaltSodaLightOnSurface,
    surfaceVariant = SeaSaltSodaLightSurfaceVariant, onSurfaceVariant = SeaSaltSodaLightOnSurfaceVariant,
    surfaceContainer = SeaSaltSodaLightSurfaceContainer, surfaceContainerHigh = SeaSaltSodaLightSurfaceContainerHigh,
    surfaceContainerHighest = SeaSaltSodaLightSurfaceContainerHighest, outline = SeaSaltSodaLightOutline,
    outlineVariant = SeaSaltSodaLightOutlineVariant, inverseSurface = SeaSaltSodaLightInverseSurface,
    inverseOnSurface = SeaSaltSodaLightInverseOnSurface, inversePrimary = SeaSaltSodaLightInversePrimary,
    surfaceTint = SeaSaltSodaLightSurfaceTint, scrim = SeaSaltSodaLightScrim
)

private val SeaSaltSodaDarkColorScheme = darkColorScheme(
    primary = SeaSaltSodaDarkPrimary, onPrimary = SeaSaltSodaDarkOnPrimary,
    primaryContainer = SeaSaltSodaDarkPrimaryContainer, onPrimaryContainer = SeaSaltSodaDarkOnPrimaryContainer,
    secondary = SeaSaltSodaDarkSecondary, onSecondary = SeaSaltSodaDarkOnSecondary,
    secondaryContainer = SeaSaltSodaDarkSecondaryContainer, onSecondaryContainer = SeaSaltSodaDarkOnSecondaryContainer,
    tertiary = SeaSaltSodaDarkTertiary, onTertiary = SeaSaltSodaDarkOnTertiary,
    tertiaryContainer = SeaSaltSodaDarkTertiaryContainer, onTertiaryContainer = SeaSaltSodaDarkOnTertiaryContainer,
    error = SeaSaltSodaDarkError, onError = SeaSaltSodaDarkOnError,
    errorContainer = SeaSaltSodaDarkErrorContainer, onErrorContainer = SeaSaltSodaDarkOnErrorContainer,
    background = SeaSaltSodaDarkBackground, onBackground = SeaSaltSodaDarkOnBackground,
    surface = SeaSaltSodaDarkSurface, onSurface = SeaSaltSodaDarkOnSurface,
    surfaceVariant = SeaSaltSodaDarkSurfaceVariant, onSurfaceVariant = SeaSaltSodaDarkOnSurfaceVariant,
    surfaceContainer = SeaSaltSodaDarkSurfaceContainer, surfaceContainerHigh = SeaSaltSodaDarkSurfaceContainerHigh,
    surfaceContainerHighest = SeaSaltSodaDarkSurfaceContainerHighest, outline = SeaSaltSodaDarkOutline,
    outlineVariant = SeaSaltSodaDarkOutlineVariant, inverseSurface = SeaSaltSodaDarkInverseSurface,
    inverseOnSurface = SeaSaltSodaDarkInverseOnSurface, inversePrimary = SeaSaltSodaDarkInversePrimary,
    surfaceTint = SeaSaltSodaDarkSurfaceTint, scrim = SeaSaltSodaDarkScrim
)

private val GrapeBubbleLightColorScheme = lightColorScheme(
    primary = GrapeBubbleLightPrimary, onPrimary = GrapeBubbleLightOnPrimary,
    primaryContainer = GrapeBubbleLightPrimaryContainer, onPrimaryContainer = GrapeBubbleLightOnPrimaryContainer,
    secondary = GrapeBubbleLightSecondary, onSecondary = GrapeBubbleLightOnSecondary,
    secondaryContainer = GrapeBubbleLightSecondaryContainer, onSecondaryContainer = GrapeBubbleLightOnSecondaryContainer,
    tertiary = GrapeBubbleLightTertiary, onTertiary = GrapeBubbleLightOnTertiary,
    tertiaryContainer = GrapeBubbleLightTertiaryContainer, onTertiaryContainer = GrapeBubbleLightOnTertiaryContainer,
    error = GrapeBubbleLightError, onError = GrapeBubbleLightOnError,
    errorContainer = GrapeBubbleLightErrorContainer, onErrorContainer = GrapeBubbleLightOnErrorContainer,
    background = GrapeBubbleLightBackground, onBackground = GrapeBubbleLightOnBackground,
    surface = GrapeBubbleLightSurface, onSurface = GrapeBubbleLightOnSurface,
    surfaceVariant = GrapeBubbleLightSurfaceVariant, onSurfaceVariant = GrapeBubbleLightOnSurfaceVariant,
    surfaceContainer = GrapeBubbleLightSurfaceContainer, surfaceContainerHigh = GrapeBubbleLightSurfaceContainerHigh,
    surfaceContainerHighest = GrapeBubbleLightSurfaceContainerHighest, outline = GrapeBubbleLightOutline,
    outlineVariant = GrapeBubbleLightOutlineVariant, inverseSurface = GrapeBubbleLightInverseSurface,
    inverseOnSurface = GrapeBubbleLightInverseOnSurface, inversePrimary = GrapeBubbleLightInversePrimary,
    surfaceTint = GrapeBubbleLightSurfaceTint, scrim = GrapeBubbleLightScrim
)

private val GrapeBubbleDarkColorScheme = darkColorScheme(
    primary = GrapeBubbleDarkPrimary, onPrimary = GrapeBubbleDarkOnPrimary,
    primaryContainer = GrapeBubbleDarkPrimaryContainer, onPrimaryContainer = GrapeBubbleDarkOnPrimaryContainer,
    secondary = GrapeBubbleDarkSecondary, onSecondary = GrapeBubbleDarkOnSecondary,
    secondaryContainer = GrapeBubbleDarkSecondaryContainer, onSecondaryContainer = GrapeBubbleDarkOnSecondaryContainer,
    tertiary = GrapeBubbleDarkTertiary, onTertiary = GrapeBubbleDarkOnTertiary,
    tertiaryContainer = GrapeBubbleDarkTertiaryContainer, onTertiaryContainer = GrapeBubbleDarkOnTertiaryContainer,
    error = GrapeBubbleDarkError, onError = GrapeBubbleDarkOnError,
    errorContainer = GrapeBubbleDarkErrorContainer, onErrorContainer = GrapeBubbleDarkOnErrorContainer,
    background = GrapeBubbleDarkBackground, onBackground = GrapeBubbleDarkOnBackground,
    surface = GrapeBubbleDarkSurface, onSurface = GrapeBubbleDarkOnSurface,
    surfaceVariant = GrapeBubbleDarkSurfaceVariant, onSurfaceVariant = GrapeBubbleDarkOnSurfaceVariant,
    surfaceContainer = GrapeBubbleDarkSurfaceContainer, surfaceContainerHigh = GrapeBubbleDarkSurfaceContainerHigh,
    surfaceContainerHighest = GrapeBubbleDarkSurfaceContainerHighest, outline = GrapeBubbleDarkOutline,
    outlineVariant = GrapeBubbleDarkOutlineVariant, inverseSurface = GrapeBubbleDarkInverseSurface,
    inverseOnSurface = GrapeBubbleDarkInverseOnSurface, inversePrimary = GrapeBubbleDarkInversePrimary,
    surfaceTint = GrapeBubbleDarkSurfaceTint, scrim = GrapeBubbleDarkScrim
)

private fun selectColorScheme(themeStyle: ThemeStyle, darkTheme: Boolean): ColorScheme {
    return when (themeStyle) {
        ThemeStyle.STRAWBERRY_SHAKE -> if (darkTheme) StrawberryShakeDarkColorScheme else StrawberryShakeLightColorScheme
        ThemeStyle.SEA_SALT_SODA -> if (darkTheme) SeaSaltSodaDarkColorScheme else SeaSaltSodaLightColorScheme
        ThemeStyle.GRAPE_BUBBLE -> if (darkTheme) GrapeBubbleDarkColorScheme else GrapeBubbleLightColorScheme
    }
}

private fun selectExtendedColorScheme(themeStyle: ThemeStyle, darkTheme: Boolean): ExtendedColorScheme {
    return when (themeStyle) {
        ThemeStyle.STRAWBERRY_SHAKE -> if (darkTheme) StrawberryShakeDarkExtendedColorScheme else StrawberryShakeLightExtendedColorScheme
        ThemeStyle.SEA_SALT_SODA -> if (darkTheme) SeaSaltSodaDarkExtendedColorScheme else SeaSaltSodaLightExtendedColorScheme
        ThemeStyle.GRAPE_BUBBLE -> if (darkTheme) GrapeBubbleDarkExtendedColorScheme else GrapeBubbleLightExtendedColorScheme
    }
}

/**
 * @param dynamicColor 保留参数以兼容设置项；糖果主题下始终使用品牌色板，忽略动态取色。
 */
@Composable
fun HuaNaErTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themeStyle: ThemeStyle = ThemeStyle.STRAWBERRY_SHAKE,
    content: @Composable () -> Unit
) {
    val colorScheme = selectColorScheme(themeStyle, darkTheme)
    val extended = selectExtendedColorScheme(themeStyle, darkTheme)

    CompositionLocalProvider(
        LocalExtendedColorScheme provides extended,
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
