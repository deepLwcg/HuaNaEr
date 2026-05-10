# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HuaNaEr (花哪儿) is a personal accounting/bookkeeping Android application built with Jetpack Compose and Material 3.

- **Package**: `cn.yajienet.huanaer`
- **minSdk**: 31 (Android 12)
- **targetSdk**: 34
- **Java version**: 11
- **Architecture**: Simple MVVM

## Features

- **收支记录**: Add income/expense transactions with categories
- **统计图表**: Monthly expense/income statistics by category
- **分类管理**: CRUD operations for transaction categories
- **预算设置**: Monthly budget tracking with progress visualization

## Build Commands

```bash
# Build the project
./gradlew build

# Clean build
./gradlew clean build

# Assemble debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug
```

## Testing Commands

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Project Structure

```
app/src/main/java/cn/yajienet/huanaer/
├── HuaNaErApplication.kt         # Application with DI setup
├── MainActivity.kt               # Main entry point
│
├── data/
│   ├── local/
│   │   ├── database/             # Room database setup
│   │   ├── entity/               # Room entities
│   │   └── dao/                  # Data access objects
│   ├── model/                    # Domain models
│   └── repository/               # Repository layer
│
├── ui/
│   ├── components/               # Shared UI components
│   ├── navigation/               # Navigation setup
│   ├── screens/                  # Feature screens (home, transaction, statistics, category, budget)
│   └── theme/                    # Material 3 theme
│
└── util/                         # Utility classes
```

## Key Dependencies

- Room (2.6.1) - Local database
- Navigation Compose (2.7.7) - Screen navigation
- ViewModel Compose (2.7.0) - MVVM architecture
- Coroutines (1.7.3) - Async operations
- Material Icons Extended - Icon library
- Vico (2.0.0-alpha.22) - Charts (available)

## Database Schema

| Table | Fields |
|-------|--------|
| transactions | id, amount, type, categoryId, date, note, createdAt, updatedAt |
| categories | id, name, icon, color, type, sortOrder, isDefault |
| budgets | id, categoryId, amount, month, year, createdAt |

## Architecture Notes

- Single-activity architecture using Jetpack Compose
- Material 3 theming with dynamic color support
- MVVM pattern with ViewModel + StateFlow
- Room database with Flow-based reactive queries
- Manual dependency injection via Application class