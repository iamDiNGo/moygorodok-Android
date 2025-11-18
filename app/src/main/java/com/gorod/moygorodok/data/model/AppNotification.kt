package com.gorod.moygorodok.data.model

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionUrl: String? = null,
    val imageColor: String? = null
)

enum class NotificationType(
    val displayName: String,
    val emoji: String,
    val color: String
) {
    NEWS("Новости", "📰", "#2196F3"),
    EVENT("События", "🎉", "#9C27B0"),
    AD("Объявления", "📢", "#FF9800"),
    DELIVERY("Доставка", "🍕", "#4CAF50"),
    TASK("Задания", "✅", "#00BCD4"),
    SYSTEM("Система", "⚙️", "#607D8B"),
    PROMO("Акции", "🎁", "#E91E63"),
    ALERT("Важное", "⚠️", "#F44336")
}

object MockNotifications {

    fun getNotifications(): List<AppNotification> {
        return listOf(
            AppNotification(
                id = "1",
                type = NotificationType.ALERT,
                title = "Внимание! Отключение воды",
                message = "Завтра с 10:00 до 18:00 будет отключено холодное водоснабжение по адресам: ул. Ленина 1-50",
                timestamp = "5 мин назад",
                isRead = false
            ),
            AppNotification(
                id = "2",
                type = NotificationType.DELIVERY,
                title = "Заказ доставлен",
                message = "Ваш заказ #1234 из «Пицца Мама» успешно доставлен. Приятного аппетита!",
                timestamp = "30 мин назад",
                isRead = false,
                imageColor = "#4CAF50"
            ),
            AppNotification(
                id = "3",
                type = NotificationType.EVENT,
                title = "Напоминание о событии",
                message = "Концерт «Городские звезды» начнется через 2 часа. Не забудьте билеты!",
                timestamp = "1 час назад",
                isRead = false
            ),
            AppNotification(
                id = "4",
                type = NotificationType.PROMO,
                title = "Скидка 20% на доставку",
                message = "Только сегодня! Используйте промокод CITY20 для скидки на любой заказ",
                timestamp = "2 часа назад",
                isRead = true
            ),
            AppNotification(
                id = "5",
                type = NotificationType.TASK,
                title = "Новый отклик на задание",
                message = "Исполнитель Алексей откликнулся на ваше задание «Ремонт крана»",
                timestamp = "3 часа назад",
                isRead = true
            ),
            AppNotification(
                id = "6",
                type = NotificationType.NEWS,
                title = "Новая статья в блоге",
                message = "В городе открылся новый парк развлечений. Читайте подробности!",
                timestamp = "5 часов назад",
                isRead = true
            ),
            AppNotification(
                id = "7",
                type = NotificationType.AD,
                title = "Ваше объявление просмотрели",
                message = "Объявление «iPhone 13 Pro» просмотрели 50 раз за последние сутки",
                timestamp = "6 часов назад",
                isRead = true
            ),
            AppNotification(
                id = "8",
                type = NotificationType.SYSTEM,
                title = "Обновление приложения",
                message = "Доступна новая версия 2.0. Обновите для получения новых функций",
                timestamp = "Вчера",
                isRead = true
            ),
            AppNotification(
                id = "9",
                type = NotificationType.NEWS,
                title = "Погода на выходные",
                message = "Ожидается солнечная погода +25°C. Отличное время для прогулок!",
                timestamp = "Вчера",
                isRead = true
            ),
            AppNotification(
                id = "10",
                type = NotificationType.PROMO,
                title = "Бесплатная доставка",
                message = "Закажите на сумму от 1000₽ и получите бесплатную доставку",
                timestamp = "2 дня назад",
                isRead = true
            )
        )
    }

    fun getUnreadCount(): Int {
        return getNotifications().count { !it.isRead }
    }
}
