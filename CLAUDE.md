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
- **Main screens** (Home, Statistics, Budget, Category) are managed by `HorizontalPager` for swipe navigation
- **Detail screens** (AddTransaction, TransactionDetail, BudgetDetail) use `NavHost` with slide transitions
- See `HuaNaErNavigation.kt` for the navigation graph

### Dependency Injection
Manual DI via `HuaNaErApplication` class:
- Access repositories via `(context.applicationContext as HuaNaErApplication).transactionRepository`
- ViewModels use `ViewModelProvider.Factory` to inject repositories
- Database is a singleton with `getDatabase(context)`

### Data Layer
- **Entities**: `TransactionEntity`, `CategoryEntity`, `BudgetEntity` (Room)
- **Models**: `Transaction`, `Category`, `Budget` (domain models)
- **Repositories**: Expose `Flow<T>` for reactive updates; use `combine()` in ViewModels

### Default Categories
On first launch, the app seeds 8 expense and 5 income categories with Chinese names. See `HuaNaErApplication.initDefaultCategories()`.

## Database Schema

| Table | Fields |
|-------|--------|
| transactions | id, amount, type, categoryId, date, note, createdAt, updatedAt |
| categories | id, name, icon, color, type, sortOrder, isDefault |
| budgets | id, categoryId, amount, month, year, createdAt |

## Key Dependencies

- Room (2.6.1) - Local database with Flow-based queries
- Navigation Compose (2.7.7) - Screen navigation
- ViewModel Compose (2.7.0) - MVVM architecture
- Coroutines (1.7.3) - Async operations
- Material Icons Extended - Icon library
- Vico (2.0.0-alpha.22) - Charts for statistics

## Utility Classes

- `DateUtils` - Date formatting and month/year calculations for queries
- `CurrencyFormat` - Currency display formatting