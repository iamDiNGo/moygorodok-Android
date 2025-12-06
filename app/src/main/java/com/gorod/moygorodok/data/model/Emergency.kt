package com.gorod.moygorodok.data.model

data class EmergencyContact(
    val id: String,
    val name: String,
    val shortName: String,
    val phone: String,
    val description: String,
    val category: EmergencyCategory,
    val icon: String,
    val color: String,
    val isMainNumber: Boolean = false
)

enum class EmergencyCategory(val displayName: String, val order: Int) {
    MAIN("Основные службы", 0),
    MEDICAL("Медицина", 1),
    SAFETY("Безопасность", 2),
    UTILITIES("Коммунальные службы", 3),
    OTHER("Другое", 4)
}

object MockEmergencyContacts {

    private val contacts = listOf(
        // Main emergency numbers
        EmergencyContact(
            id = "1",
            name = "Единый номер экстренных служб",
            shortName = "Экстренные службы",
            phone = "112",
            description = "Единый номер для вызова всех экстренных служб",
            category = EmergencyCategory.MAIN,
            icon = "🆘",
            color = "#F44336",
            isMainNumber = true
        ),
        EmergencyContact(
            id = "2",
            name = "Полиция",
            shortName = "Полиция",
            phone = "102",
            description = "Вызов полиции в случае преступления или угрозы безопасности",
            category = EmergencyCategory.MAIN,
            icon = "👮",
            color = "#3F51B5",
            isMainNumber = true
        ),
        EmergencyContact(
            id = "3",
            name = "Скорая медицинская помощь",
            shortName = "Скорая помощь",
            phone = "103",
            description = "Вызов скорой помощи при угрозе жизни и здоровью",
            category = EmergencyCategory.MAIN,
            icon = "🚑",
            color = "#E91E63",
            isMainNumber = true
        ),
        EmergencyContact(
            id = "4",
            name = "Пожарная охрана",
            shortName = "Пожарная",
            phone = "101",
            description = "Вызов пожарной службы при возгорании",
            category = EmergencyCategory.MAIN,
            icon = "🚒",
            color = "#FF5722",
            isMainNumber = true
        ),
        EmergencyContact(
            id = "5",
            name = "Газовая служба",
            shortName = "Газ",
            phone = "104",
            description = "При запахе газа или утечке",
            category = EmergencyCategory.MAIN,
            icon = "🔥",
            color = "#FF9800",
            isMainNumber = true
        ),

        // Medical
        EmergencyContact(
            id = "6",
            name = "Детская скорая помощь",
            shortName = "Детская скорая",
            phone = "103",
            description = "Скорая помощь для детей до 18 лет",
            category = EmergencyCategory.MEDICAL,
            icon = "👶",
            color = "#4CAF50"
        ),
        EmergencyContact(
            id = "7",
            name = "Психологическая помощь",
            shortName = "Психолог",
            phone = "8-800-2000-122",
            description = "Бесплатная психологическая помощь круглосуточно",
            category = EmergencyCategory.MEDICAL,
            icon = "🧠",
            color = "#9C27B0"
        ),
        EmergencyContact(
            id = "8",
            name = "Наркологическая помощь",
            shortName = "Нарколог",
            phone = "8-800-222-0-222",
            description = "Помощь при зависимостях",
            category = EmergencyCategory.MEDICAL,
            icon = "💊",
            color = "#00BCD4"
        ),

        // Safety
        EmergencyContact(
            id = "9",
            name = "МЧС",
            shortName = "МЧС",
            phone = "112",
            description = "Министерство по чрезвычайным ситуациям",
            category = EmergencyCategory.SAFETY,
            icon = "🛡️",
            color = "#FF5722"
        ),
        EmergencyContact(
            id = "10",
            name = "ГИБДД",
            shortName = "ГИБДД",
            phone = "102",
            description = "Дорожно-транспортные происшествия",
            category = EmergencyCategory.SAFETY,
            icon = "🚗",
            color = "#607D8B"
        ),
        EmergencyContact(
            id = "11",
            name = "Телефон доверия",
            shortName = "Доверие",
            phone = "8-800-2000-122",
            description = "Анонимная линия психологической помощи",
            category = EmergencyCategory.SAFETY,
            icon = "🤝",
            color = "#795548"
        ),
        EmergencyContact(
            id = "12",
            name = "Помощь женщинам",
            shortName = "Женская линия",
            phone = "8-800-7000-600",
            description = "Помощь при домашнем насилии",
            category = EmergencyCategory.SAFETY,
            icon = "👩",
            color = "#E91E63"
        ),

        // Utilities
        EmergencyContact(
            id = "13",
            name = "Водоканал",
            shortName = "Вода",
            phone = "8-800-555-0-888",
            description = "Аварии водоснабжения и канализации",
            category = EmergencyCategory.UTILITIES,
            icon = "💧",
            color = "#2196F3"
        ),
        EmergencyContact(
            id = "14",
            name = "Электросети",
            shortName = "Электричество",
            phone = "8-800-220-0-220",
            description = "Аварии электроснабжения",
            category = EmergencyCategory.UTILITIES,
            icon = "⚡",
            color = "#FFC107"
        ),
        EmergencyContact(
            id = "15",
            name = "Теплосети",
            shortName = "Отопление",
            phone = "8-800-100-0-100",
            description = "Аварии отопления",
            category = EmergencyCategory.UTILITIES,
            icon = "🌡️",
            color = "#FF5722"
        ),

        // Other
        EmergencyContact(
            id = "16",
            name = "Антитеррор",
            shortName = "Антитеррор",
            phone = "8-800-100-12-17",
            description = "Сообщить о подозрительных предметах или людях",
            category = EmergencyCategory.OTHER,
            icon = "🚨",
            color = "#F44336"
        ),
        EmergencyContact(
            id = "17",
            name = "Защита прав потребителей",
            shortName = "Роспотребнадзор",
            phone = "8-800-555-49-43",
            description = "Жалобы на качество товаров и услуг",
            category = EmergencyCategory.OTHER,
            icon = "📋",
            color = "#009688"
        )
    )

    fun getContacts(): List<EmergencyContact> = contacts

    fun getMainContacts(): List<EmergencyContact> = contacts.filter { it.isMainNumber }

    fun getContactsByCategory(category: EmergencyCategory): List<EmergencyContact> =
        contacts.filter { it.category == category }

    fun getContactById(id: String): EmergencyContact? = contacts.find { it.id == id }
}
