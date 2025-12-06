package com.gorod.moygorodok.data.model

data class Complaint(
    val id: String,
    val category: ComplaintCategory,
    val title: String,
    val description: String,
    val address: String,
    val images: List<String> = emptyList(),
    val status: ComplaintStatus = ComplaintStatus.NEW,
    val createdAt: String,
    val authorName: String,
    val authorPhone: String
)

enum class ComplaintCategory(
    val displayName: String,
    val emoji: String,
    val color: String
) {
    ROADS("Дороги и тротуары", "🛣️", "#FF6B6B"),
    LIGHTING("Освещение", "💡", "#FFE66D"),
    GARBAGE("Мусор и уборка", "🗑️", "#4ECDC4"),
    PLAYGROUND("Детские площадки", "🎠", "#95E1D3"),
    PARKING("Парковка", "🅿️", "#A8E6CF"),
    UTILITIES("ЖКХ", "🏠", "#DDA0DD"),
    TRANSPORT("Общественный транспорт", "🚌", "#74B9FF"),
    LANDSCAPING("Благоустройство", "🌳", "#00B894"),
    SAFETY("Безопасность", "⚠️", "#FDCB6E"),
    OTHER("Другое", "📝", "#B2BEC3")
}

enum class ComplaintStatus(
    val displayName: String,
    val color: String
) {
    NEW("Новое", "#2196F3"),
    IN_PROGRESS("В работе", "#FF9800"),
    RESOLVED("Решено", "#4CAF50"),
    REJECTED("Отклонено", "#F44336")
}

object MockComplaints {

    fun getComplaints(): List<Complaint> {
        return listOf(
            Complaint(
                id = "1",
                category = ComplaintCategory.ROADS,
                title = "Яма на дороге",
                description = "Большая яма на проезжей части, опасно для автомобилей",
                address = "ул. Ленина, 45",
                status = ComplaintStatus.IN_PROGRESS,
                createdAt = "15 ноября 2024",
                authorName = "Иван Петров",
                authorPhone = "+7 900 123-45-67"
            ),
            Complaint(
                id = "2",
                category = ComplaintCategory.LIGHTING,
                title = "Не работает фонарь",
                description = "Уличный фонарь не горит уже неделю",
                address = "ул. Пушкина, 12",
                status = ComplaintStatus.NEW,
                createdAt = "14 ноября 2024",
                authorName = "Мария Сидорова",
                authorPhone = "+7 900 234-56-78"
            ),
            Complaint(
                id = "3",
                category = ComplaintCategory.GARBAGE,
                title = "Переполненные контейнеры",
                description = "Мусорные контейнеры не вывозились несколько дней",
                address = "ул. Гагарина, 8",
                status = ComplaintStatus.RESOLVED,
                createdAt = "12 ноября 2024",
                authorName = "Алексей Козлов",
                authorPhone = "+7 900 345-67-89"
            )
        )
    }

    fun getCategories(): List<ComplaintCategory> {
        return ComplaintCategory.values().toList()
    }
}
