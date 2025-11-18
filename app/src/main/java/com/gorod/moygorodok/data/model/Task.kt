package com.gorod.moygorodok.data.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val price: TaskPrice,
    val location: String,
    val address: String,
    val date: String,
    val time: String? = null,
    val imageColor: String,
    val executor: TaskExecutor? = null,
    val isUrgent: Boolean = false,
    val views: Int = 0,
    val responses: Int = 0,
    val authorName: String,
    val authorPhone: String,
    val createdAt: String,
    val isFavorite: Boolean = false
)

data class TaskPrice(
    val type: TaskPriceType,
    val amount: Double? = null,
    val maxAmount: Double? = null
)

enum class TaskPriceType(val displayName: String) {
    FIXED("Фиксированная"),
    NEGOTIABLE("Договорная"),
    HOURLY("Почасовая"),
    RANGE("От и до")
}

data class TaskExecutor(
    val id: String,
    val name: String,
    val rating: Double,
    val reviewCount: Int,
    val completedTasks: Int,
    val avatarColor: String
)

enum class TaskCategory(val displayName: String, val emoji: String, val color: String) {
    REPAIR("Ремонт", "🔧", "#FF6B6B"),
    CLEANING("Уборка", "🧹", "#4ECDC4"),
    MOVING("Грузоперевозки", "🚚", "#45B7D1"),
    PLUMBING("Сантехника", "🚿", "#96CEB4"),
    ELECTRICAL("Электрика", "⚡", "#FFEAA7"),
    CONSTRUCTION("Строительство", "🏗️", "#DDA0DD"),
    BEAUTY("Красота", "💅", "#FFB6C1"),
    TUTORING("Репетиторство", "📚", "#98D8C8")
}

object MockTasks {
    private val tasks = listOf(
        Task(
            id = "1",
            title = "Собрать мебель IKEA",
            description = "Нужно собрать шкаф-купе и комод из IKEA. Все детали и инструкция в наличии. Желательно опыт сборки мебели. Инструменты есть свои.",
            category = TaskCategory.REPAIR,
            price = TaskPrice(TaskPriceType.FIXED, 3000.0),
            location = "Центральный район",
            address = "ул. Ленина, 45, кв. 12",
            date = "Сегодня",
            time = "После 18:00",
            imageColor = "#FF6B6B",
            isUrgent = true,
            views = 45,
            responses = 3,
            authorName = "Анна М.",
            authorPhone = "+7 (999) 123-45-67",
            createdAt = "2 часа назад"
        ),
        Task(
            id = "2",
            title = "Генеральная уборка квартиры",
            description = "Требуется генеральная уборка 3-комнатной квартиры 85 кв.м. Мытье окон, уборка кухни, санузлов, всех комнат. Моющие средства предоставлю.",
            category = TaskCategory.CLEANING,
            price = TaskPrice(TaskPriceType.RANGE, 4000.0, 6000.0),
            location = "Советский район",
            address = "пр. Мира, 78, кв. 56",
            date = "Завтра",
            time = "10:00 - 14:00",
            imageColor = "#4ECDC4",
            executor = TaskExecutor(
                id = "e1",
                name = "Мария К.",
                rating = 4.9,
                reviewCount = 127,
                completedTasks = 234,
                avatarColor = "#E8D5B7"
            ),
            views = 89,
            responses = 7,
            authorName = "Петр В.",
            authorPhone = "+7 (999) 234-56-78",
            createdAt = "5 часов назад"
        ),
        Task(
            id = "3",
            title = "Перевезти диван",
            description = "Нужно перевезти большой угловой диван из одной квартиры в другую. Расстояние примерно 5 км. Нужны грузчики и газель.",
            category = TaskCategory.MOVING,
            price = TaskPrice(TaskPriceType.FIXED, 5000.0),
            location = "Ленинский район",
            address = "ул. Гагарина, 23",
            date = "20 ноября",
            time = "09:00",
            imageColor = "#45B7D1",
            views = 34,
            responses = 5,
            authorName = "Сергей Н.",
            authorPhone = "+7 (999) 345-67-89",
            createdAt = "Вчера"
        ),
        Task(
            id = "4",
            title = "Починить смеситель",
            description = "Течет смеситель на кухне. Возможно нужна замена картриджа или всего смесителя. Нужен осмотр и ремонт.",
            category = TaskCategory.PLUMBING,
            price = TaskPrice(TaskPriceType.NEGOTIABLE),
            location = "Кировский район",
            address = "ул. Пушкина, 10, кв. 5",
            date = "Сегодня",
            time = "В любое время",
            imageColor = "#96CEB4",
            isUrgent = true,
            views = 67,
            responses = 4,
            authorName = "Елена Д.",
            authorPhone = "+7 (999) 456-78-90",
            createdAt = "1 час назад"
        ),
        Task(
            id = "5",
            title = "Установить люстру",
            description = "Нужно установить новую люстру в гостиной. Старую демонтировать. Люстра на 5 рожков, вес около 3 кг.",
            category = TaskCategory.ELECTRICAL,
            price = TaskPrice(TaskPriceType.FIXED, 1500.0),
            location = "Центральный район",
            address = "ул. Советская, 89, кв. 34",
            date = "18 ноября",
            time = "После 17:00",
            imageColor = "#FFEAA7",
            executor = TaskExecutor(
                id = "e2",
                name = "Алексей Р.",
                rating = 5.0,
                reviewCount = 89,
                completedTasks = 156,
                avatarColor = "#B8D4E3"
            ),
            views = 23,
            responses = 2,
            authorName = "Ольга К.",
            authorPhone = "+7 (999) 567-89-01",
            createdAt = "3 часа назад"
        ),
        Task(
            id = "6",
            title = "Положить плитку в ванной",
            description = "Требуется положить плитку в ванной комнате 4 кв.м. Плитка куплена. Нужен опытный плиточник. Демонтаж старой плитки включен.",
            category = TaskCategory.CONSTRUCTION,
            price = TaskPrice(TaskPriceType.RANGE, 15000.0, 20000.0),
            location = "Октябрьский район",
            address = "ул. Красная, 156, кв. 78",
            date = "22-25 ноября",
            imageColor = "#DDA0DD",
            views = 112,
            responses = 8,
            authorName = "Дмитрий Л.",
            authorPhone = "+7 (999) 678-90-12",
            createdAt = "1 день назад"
        ),
        Task(
            id = "7",
            title = "Маникюр на дому",
            description = "Ищу мастера маникюра для выполнения маникюра с покрытием гель-лаком на дому. Желательно с выездом и своими материалами.",
            category = TaskCategory.BEAUTY,
            price = TaskPrice(TaskPriceType.FIXED, 1200.0),
            location = "Советский район",
            address = "пр. Победы, 45, кв. 23",
            date = "19 ноября",
            time = "14:00",
            imageColor = "#FFB6C1",
            views = 56,
            responses = 6,
            authorName = "Наталья С.",
            authorPhone = "+7 (999) 789-01-23",
            createdAt = "6 часов назад"
        ),
        Task(
            id = "8",
            title = "Репетитор по математике",
            description = "Ищу репетитора по математике для ученика 9 класса. Подготовка к ОГЭ. 2 раза в неделю по 1.5 часа. Желательно с опытом подготовки к экзаменам.",
            category = TaskCategory.TUTORING,
            price = TaskPrice(TaskPriceType.HOURLY, 1000.0),
            location = "Ленинский район",
            address = "ул. Школьная, 12",
            date = "По договоренности",
            imageColor = "#98D8C8",
            executor = TaskExecutor(
                id = "e3",
                name = "Ирина В.",
                rating = 4.8,
                reviewCount = 45,
                completedTasks = 78,
                avatarColor = "#F5E6CC"
            ),
            views = 78,
            responses = 4,
            authorName = "Марина П.",
            authorPhone = "+7 (999) 890-12-34",
            createdAt = "2 дня назад"
        )
    )

    fun getTasks(): List<Task> = tasks

    fun getTaskById(id: String): Task? = tasks.find { it.id == id }

    fun getTasksByCategory(category: TaskCategory): List<Task> =
        tasks.filter { it.category == category }

    fun searchTasks(query: String): List<Task> =
        tasks.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.location.contains(query, ignoreCase = true)
        }
}
