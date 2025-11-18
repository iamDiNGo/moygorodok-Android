# MoyGorodok - Архитектура проекта

## Архитектура: MVVM (Model-View-ViewModel)

Проект использует архитектурный паттерн MVVM с использованием Android Jetpack компонентов.

### Структура слоев

#### 1. Model (Модель)
- **Назначение**: Управление данными и бизнес-логикой
- **Компоненты**:
  - Data классы (Entity)
  - Repository (репозитории для работы с данными)
  - Data Source (локальные и удаленные источники данных)
  - Room Database (локальное хранилище)
  - Network API (работа с сервером)

#### 2. View (Представление)
- **Назначение**: Отображение UI и обработка взаимодействия с пользователем
- **Компоненты**:
  - Activity
  - Fragment
  - ViewBinding для связывания с layout
  - RecyclerView Adapters
  - Custom Views

#### 3. ViewModel (Модель представления)
- **Назначение**: Связующее звено между View и Model
- **Компоненты**:
  - ViewModel классы
  - LiveData для реактивного обновления UI
  - Обработка UI логики
  - Управление состоянием UI

### Принципы

1. **Однонаправленный поток данных**
   - View наблюдает за ViewModel через LiveData
   - View отправляет события в ViewModel
   - ViewModel взаимодействует с Model
   - Model возвращает данные через Repository

2. **Разделение ответственности**
   - View не содержит бизнес-логику
   - ViewModel не знает о View
   - Model независим от UI

3. **Lifecycle-aware компоненты**
   - ViewModel переживает изменения конфигурации
   - LiveData автоматически управляет подписками

### Структура пакетов

```
com.gorod.moygorodok/
├── data/
│   ├── local/          # Room Database, DAO
│   ├── remote/         # Retrofit, API
│   ├── model/          # Data классы
│   └── repository/     # Repository классы
├── ui/
│   ├── home/          # HomeFragment + HomeViewModel
│   ├── dashboard/     # DashboardFragment + DashboardViewModel
│   ├── notifications/ # NotificationsFragment + NotificationsViewModel
│   └── adapters/      # RecyclerView Adapters
├── viewmodel/         # ViewModel классы
└── util/              # Утилиты и хелперы
```

### Используемые библиотеки

- **AndroidX Core KTX** - Kotlin extensions
- **Lifecycle (LiveData, ViewModel)** - Lifecycle-aware компоненты
- **Navigation Component** - Навигация между фрагментами
- **ViewBinding** - Безопасная работа с views
- **Material Design** - UI компоненты

### Пример взаимодействия

```
User Action → Fragment → ViewModel → Repository → Data Source
                  ↑           ↓
                  └─ LiveData ┘
```

## Конфигурация проекта

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Language**: Kotlin
- **Build System**: Gradle (Kotlin DSL)
- **Java Version**: 11
