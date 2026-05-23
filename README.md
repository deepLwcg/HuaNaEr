# 花哪儿 (HuaNaEr)

一款简洁优雅的个人记账 Android 应用，采用 Glassmorphism 柔光弥散风格设计，支持多主题切换、预算管理和数据统计。

## 功能特性

### 核心功能
- **记账管理** - 快速记录收入与支出，支持分类、日期、备注
- **预算追踪** - 设置月度预算，实时追踪支出进度
- **统计分析** - 甜甜圈图表展示支出分布，迷你趋势线
- **分类管理** - 自定义收支分类，支持图标和颜色

### 快捷入口
- **桌面小部件** - 一键添加交易，无需打开应用
- **应用快捷方式** - 从桌面快捷添加记账

### 个性化
- **多主题风格** - 三种马卡龙色系主题可切换
  - 薄荷奶绿 (MintMilk) - 清新薄荷绿配柔和奶白
  - 薰衣草紫 (Lavender) - 优雅紫色调配暖黄点缀
  - 暖阳蜜桃 (WarmPeach) - 温暖蜜桃橙配奶油白
- **深色模式** - 支持浅色/深色/跟随系统
- **Material You** - 支持 Android 12+ 动态取色
- **自定义月起始日** - 灵活设置记账周期

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Kotlin | 2.0.21 | 主要开发语言 |
| Jetpack Compose | BOM 2024.11.00 | 现代 UI 框架 |
| Material 3 | - | 最新设计规范 |
| Room | 2.6.1 | 本地数据库 |
| ViewModel | 2.7.0 | MVVM 架构 |
| Navigation Compose | 2.7.7 | 页面导航 |
| Coroutines | 1.7.3 | 异步处理 |
| DataStore | 1.0.0 | 设置存储 |
| Vico | 2.0.0-alpha.22 | 图表库 |

## 架构

```
├── data/
│   ├── local/
│   │   ├── database/     # Room 数据库
│   │   ├── dao/          # 数据访问对象
│   │   └── entity/       # 数据实体
│   ├── model/            # 领域模型
│   ├── repository/       # 数据仓库
│   └── datastore/        # 设置存储
├── ui/
│   ├── screens/          # 各功能页面
│   │   ├── home/         # 首页
│   │   ├── transaction/  # 记账
│   │   ├── statistics/   # 统计
│   │   ├── budget/       # 预算
│   │   ├── category/     # 分类管理
│   │   └── settings/     # 设置
│   ├── components/       # UI 组件
│   │   ├── glassmorphism/ # 柔光弥散组件库
│   ├── navigation/       # 导航
│   └── theme/            # 主题
├── util/                 # 工具类
├── widget/               # 桌面小部件
└── HuaNaErApplication.kt # 应用入口
```

### 数据层设计
- **Entity** - Room 数据表实体 (`TransactionEntity`, `CategoryEntity`, `BudgetEntity`)
- **Model** - 颠域模型，包含关联数据 (`Transaction`, `Category`, `Budget`)
- **DAO** - 使用 JOIN 查询一次获取关联数据
- **Repository** - 暴露 `Flow<T>` 实现响应式更新

### ViewModel 响应式模式
使用 `flatMapLatest` 和 `combine` 处理时间范围查询：
```kotlin
val uiState = dateRange.flatMapLatest { (start, end) ->
    combine(
        repository.getByDateRange(start, end),
        repository.getTotalByTypeAndDateRange(type, start, end)
    ) { ... }
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())
```

## 构建运行

### 环境要求
- Android SDK 34
- JDK 11
- Android 12+ 设备 (minSdk 31)

### 构建命令
```bash
# 构建项目
gradlew.bat build

# 清理构建
gradlew.bat clean build

# 生成 Debug APK
gradlew.bat assembleDebug

# 安装到设备
gradlew.bat installDebug

# 运行单元测试
gradlew.bat test

# 运行仪器化测试
gradlew.bat connectedAndroidTest
```

## 数据库结构

### transactions 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | LONG | 主键 |
| amount | DOUBLE | 金额 |
| type | STRING | 收入/支出 |
| categoryId | LONG | 分类 ID (外键) |
| date | LONG | 日期时间戳 |
| note | STRING | 备注 |
| createdAt | LONG | 创建时间 |
| updatedAt | LONG | 更新时间 |

### categories 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | LONG | 主键 |
| name | STRING | 分类名称 |
| icon | STRING | 图标名称 |
| color | INT | 颜色值 |
| type | STRING | 收入/支出 |
| sortOrder | INT | 排序权重 |
| isDefault | BOOLEAN | 是否默认 |

### budgets 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | LONG | 主键 |
| categoryId | LONG | 分类 ID (外键) |
| amount | DOUBLE | 预算金额 |
| month | INT | 月份 |
| year | INT | 年份 |
| createdAt | LONG | 创建时间 |

## UI 组件库

### Glassmorphism 柔光弥散组件
| 组件 | 说明 |
|------|------|
| `GlassCard` | 半透明玻璃卡片，白色边框叠加 |
| `NeumorphicCard` | 新拟态风格卡片 |
| `NeumorphicFab` | 弹跳动画 FAB |
| `NeumorphicNumberPad` | 圆形按键数字键盘，弹跳动画+触觉反馈 |
| `NeumorphicSwitch` | 自定义开关，弹跳拇指偏移 |
| `PillFilter` | 药丸形状筛选器，颜色/边框动画过渡 |
| `BudgetRing` | 预算进度环形指示器 |
| `GlowBackground` | 弥散光斑背景效果 |
| `ShimmerBox` | 加载骨架屏闪烁效果 |

### 动画规范
- **页面过渡** - `FastOutSlowInEasing`, 300ms
- **交互动画** - Spring 弹跳动画 `DampingRatioMediumBouncy`

## 默认分类

首次启动自动初始化 13 个默认分类：

**支出 (8个)**: 餐饮、交通、购物、娱乐、居住、医疗、教育、其他
**收入 (5个)**: 工资、奖金、投资、兼职、其他

## 许可证

本项目仅供学习和个人使用。

---

**花哪儿** - 记录每一笔，心中有数。