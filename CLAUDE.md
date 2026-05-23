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
- **Main screens** (Home, Statistics, Budget, Settings) are managed by `HorizontalPager` for swipe navigation — they share a single `"main"` NavHost route, with page switching via `pagerState.animateScrollToPage()`
- **Detail screens** (AddTransaction, TransactionDetail, BudgetDetail, TransactionList, CategoryManage) use `NavHost` with slide transitions
- Route definitions in `Screen.kt`:
  - `home`, `statistics`, `budgets`, `settings` — tab identifiers (used by `BottomNavItem`, not NavHost)
  - Detail routes: `transactions/add`, `transactions/{id}`, `budgets/{id}`, `categories`
- Bottom navigation bar uses `CandyCard` with spring-animated `NavBarItem`s (scale bounce on selection)
- AddTransaction has two entry points: full screen via NavHost (`Screen.AddTransaction`), and `AddTransactionBottomSheet` (ModalBottomSheet from Home)
- `StatisticsViewModel` is shared in TopAppBar to enable month/year picker across the Statistics screen

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

### UiState Convention
All ViewModels expose a single `uiState: StateFlow<UiState>` property. The `UiState` data class typically contains:
- `isLoading: Boolean` — initial true, false after first data load
- `data: List<T>` or specific fields — the actual display data
- `error: String?` — error message if any

### Default Categories
On first launch, the app seeds 8 expense and 5 income categories with Chinese names. See `HuaNaErApplication.initDefaultCategories()`.

## Theme and Styling

### Multi-Theme Style System (多巴胺糖果)
The app supports 3 candy theme styles selectable via `ThemeStyle` enum, persisted in DataStore:

| Style | Key | Description |
|-------|-----|-------------|
| 草莓奶昔 | `STRAWBERRY_SHAKE` | Pink #FF6B9D + yellow #FFE66D — default |
| 海盐汽水 | `SEA_SALT_SODA` | Cyan #4ECDC4 + yellow #FFE66D |
| 葡萄泡泡 | `GRAPE_BUBBLE` | Purple #C3B1E1 + orange #FFA07A |

Each style defines a complete M3 `ColorScheme` (light + dark) and an `ExtendedColorScheme` (financial colors: income #00E5A0, expense #FF6B6B). `HuaNaErTheme` always uses brand colors (ignores Material You dynamic color).

> ⚠️ README.md uses outdated Chinese theme names (MintMilk, Lavender, WarmPeach) and incorrectly claims Material You support. The code's `ThemeStyle` enum values (STRAWBERRY_SHAKE, SEA_SALT_SODA, GRAPE_BUBBLE) are authoritative.

Access extended colors via:
```kotlin
val colors = extendedColorScheme()
Text(text = amount, color = colors.income)
```

### Candy UI Component Library
多巴胺糖果设计系统 in `ui/components/candy/` (legacy `glassmorphism/` retained until full migration):

| Component | Purpose |
|-----------|---------|
| `CandyCard` | Large-radius card with colored shadow + optional gradient |
| `CandyFab` | 68dp candy-ball FAB with gradient + bounce |
| `GummyButton` | Gummy press scale animation button |
| `EmojiCategoryChip` | Circular emoji category picker with glow |
| `BouncyNumber` | Per-digit bounce number display |
| `GummyNumberPad` | Fruit-candy number pad with haptics |
| `SegmentedGummy` | Income/expense segmented control |
| `PillChip` | Candy pill filter chips |
| `ProgressRing` | Canvas ring progress with gradient |
| `ConfettiBurst` | Candy bean particle celebration |
| `EmptyStateSticker` | Floating emoji empty state |
| `ShimmerCandy` | Candy-colored skeleton loaders |

### Glassmorphism UI Component Library
Legacy 柔光弥散风格组件 in `ui/components/glassmorphism/`:

| Component | Purpose |
|-----------|---------|
| `GlassCard` | Frosted glass card — semi-transparent bg + white border + subtle inner gradient |
| `NeumorphicCard` | Soft UI card with inset/outset shadow |
| `NeumorphicFab` | Bounce-animated FAB |
| `NeumorphicNumberPad` | Circular haptic number pad with bounce |
| `NeumorphicSwitch` | Bouncy thumb-offset toggle |
| `PillFilter` | Pill-shaped filter with color/border animation |
| `BudgetRing` | Budget progress ring indicator |
| `GlowBackground` | Diffused glow background effect |
| `ShimmerBox` | Loading skeleton shimmer |

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
- `themeStyle` - Visual theme style (`ThemeStyle`: STRAWBERRY_SHAKE, SEA_SALT_SODA, GRAPE_BUBBLE)
- `dynamicColorEnabled` - Stored but ignored; candy themes always use brand colors
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
