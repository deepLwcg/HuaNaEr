# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HuaNaEr (花哪儿) is a personal accounting/bookkeeping Android application built with Jetpack Compose and Material 3.

- **Package**: `cn.yajienet.huanaer`
- **minSdk**: 31 (Android 12)
- **targetSdk**: 34
- **Java version**: 11
- **Architecture**: Simple MVVM with manual dependency injection

## Build Commands

```bash
# Build the project (Windows)
gradlew.bat build

# Clean build
gradlew.bat clean build

# Assemble debug APK
gradlew.bat assembleDebug

# Install on device
gradlew.bat installDebug

# Run unit tests
gradlew.bat test

# Run instrumented tests
gradlew.bat connectedAndroidTest
```

## Architecture

### Navigation Structure
The app uses a hybrid navigation approach:
- **Main screens** (Home, Statistics, Budget, Settings) are managed by `HorizontalPager` for swipe navigation
- **Detail screens** (AddTransaction, TransactionDetail, BudgetDetail, TransactionList, CategoryManage) use `NavHost` with slide transitions
- See `HuaNaErNavigation.kt` for the navigation graph and `Screen.kt` for route definitions
- Bottom navigation bar uses `GlassCard` with spring-animated `NavBarItem`s
- AddTransaction has two entry points: full screen via NavHost, and `AddTransactionBottomSheet` (ModalBottomSheet from Home)

### Dependency Injection
Manual DI via `HuaNaErApplication` class:
- Access repositories via `(context.applicationContext as HuaNaErApplication).transactionRepository`
- ViewModels use `ViewModelProvider.Factory` to inject repositories
- Database is a singleton with `getDatabase(context)`

### Data Layer
- **Entities**: `TransactionEntity`, `CategoryEntity`, `BudgetEntity` (Room)
- **Models**: `Transaction`, `Category`, `Budget` (domain models with category info embedded via `@Immutable`)
- **Repositories**: Expose `Flow<T>` for reactive updates; use `combine()` and `flatMapLatest` in ViewModels
- **JOIN queries**: DAOs return `TransactionWithCategory`/`BudgetWithCategory` to fetch related data in one query; repositories map these to domain models
- **BudgetDao**: Uses `getCategorySpentsBatch` for batch spent-amount calculation across categories

### ViewModel Reactive Patterns
Use `flatMapLatest` for time-based queries that need to refresh when parameters change:
```kotlin
private val dateRange = combine(_selectedMonth, _selectedYear) { month, year ->
    DateUtils.getMonthStartTime(month, year) to DateUtils.getMonthEndTime(month, year)
}

val uiState: StateFlow<UiState> = dateRange.flatMapLatest { (startTime, endTime) ->
    combine(
        repository.getByDateRange(startTime, endTime),
        repository.getTotalByTypeAndDateRange(type, startTime, endTime)
    ) { ... }
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState(isLoading = true))
```

### Default Categories
On first launch, the app seeds 8 expense and 5 income categories with Chinese names. See `HuaNaErApplication.initDefaultCategories()`.

## Theme and Styling

### Multi-Theme Style System
The app supports 3 theme styles selectable via `ThemeStyle` enum, persisted in DataStore:

| Style | Key | Description |
|-------|-----|-------------|
| 薄荷清风 (Mint Breeze) | `MINT_BREEZE` | Green primary (#00C896), gold accent — default |
| 落日余晖 (Sunset Glow) | `SUNSET_GLOW` | Coral primary (#FF6B6B), amber accent |
| 午夜霓虹 (Midnight Neon) | `MIDNIGHT_NEON` | Purple primary (#7B2FFF), cyan accent (#00E5FF) |

Each style defines a complete M3 `ColorScheme` (light + dark) and an `ExtendedColorScheme` (financial colors). The old blue-purple scheme exists in `Color.kt` for backward compatibility but is not selectable via `ThemeStyle`.

Theme is applied in `HuaNaErTheme()` composable, which selects color schemes based on `themeStyle + darkTheme + dynamicColor`. Access extended colors via:
```kotlin
val colors = extendedColorScheme()
Text(text = amount, color = colors.income)
```

### Neubru UI Component Library
Custom "Neo-brutalist" design system in `ui/components/neubru/`:

| Component | Purpose |
|-----------|---------|
| `NeubruCard` | Card with offset shadow + thick border (default container: `surfaceContainerHigh`) |
| `GlassCard` | Semi-transparent surface with white border overlay (used for nav bar, balance display) |
| `NeubruNumberPad` | Circular-key number pad with bounce animation + haptic feedback (for transaction amounts) |
| `NeubruFab` | FAB with offset shadow + bounce animation + haptic feedback |
| `NeubruSwitch` | Custom switch with bouncy thumb offset + thick borders |
| `PillChip` | Pill-shaped chip with animated color/border transitions (type toggle, time range selector) |
| `BouncyIconButton` | Press-to-shrink icon button with spring animation (category selection) |
| `AnimatedCounter` | Smoothly animated number counter (cents-based for precision) |
| `ShimmerPlaceholder` | Loading shimmer effects (ShimmerLine, ShimmerCircle, ShimmerCard) |
| `CelebrationOverlay` | Confetti particle animation on transaction save |
| `NeubruTheme` | `NeubruElevation` composition local for customizing shadow offsets |

### Animation Convention
Use `FastOutSlowInEasing` with 300ms duration for screen transitions:
```kotlin
animationSpec = tween(300, easing = FastOutSlowInEasing)
```

For item animations (swipe, progress, bounce), use spring animations:
```kotlin
animationSpec = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)
```

### Canvas-Based Custom Drawings
- **DonutChart**: Custom Canvas-drawn donut chart in StatisticsScreen (Vico is in dependencies but not used for donut)
- **SparkLine**: Mini sparkline in SummaryCard showing daily expense trend (data from `HomeViewModel.getDailyTotalsAsPairs`)

## Database Schema

| Table | Fields |
|-------|--------|
| transactions | id, amount, type, categoryId, date, note, createdAt, updatedAt |
| categories | id, name, icon, color, type, sortOrder, isDefault |
| budgets | id, categoryId, amount, month, year, createdAt |

Foreign keys: `transactions.categoryId` → `categories.id` (CASCADE), `budgets.categoryId` → `categories.id` (CASCADE)

## Key Dependencies

Dependencies managed via Gradle Version Catalog (`gradle/libs.versions.toml`):
- Room (2.6.1) - Local database with Flow-based queries
- Navigation Compose (2.7.7) - Screen navigation
- ViewModel Compose (2.7.0) - MVVM architecture
- Coroutines (1.7.3) - Async operations
- Vico (2.0.0-alpha.22) - Charts library (currently unused for donut — custom Canvas used)
- DataStore Preferences (1.0.0) - User settings storage
- Compose BOM (2024.11.00) - Compose version management

## DataStore Settings

User preferences stored in `SettingsDataStore`:
- `themeMode` - Light/Dark/System theme (`ThemeMode` enum)
- `themeStyle` - Visual theme style (`ThemeStyle` enum: MINT_BREEZE, SUNSET_GLOW, MIDNIGHT_NEON)
- `dynamicColorEnabled` - Material You dynamic colors
- `monthStartDay` - Custom month start day for accounting
- `defaultExpenseCategoryId` / `defaultIncomeCategoryId` - Quick add defaults
- `largeAmountThreshold` - Threshold for large amount warnings

## Quick Entry Features

### Desktop Widget
The app provides a desktop widget (`AddTransactionWidget`) for quick transaction entry:
- Widget layout defined in `res/layout/widget_add_transaction.xml`
- Widget configuration in `res/xml/widget_info.xml`
- Tapping the widget launches MainActivity with `ACTION_ADD_TRANSACTION` intent, navigating directly to AddTransactionScreen

### App Shortcuts
Static app shortcuts defined in `res/xml/shortcuts.xml` allow quick access to add transaction from launcher.

### Splash Screen
On normal launch, `SplashScreen` displays for 2 seconds before showing main content. Shortcut/widget launches bypass splash screen.
