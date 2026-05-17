package com.gorod.moygorodok.data.model

sealed class HomeWidget {
    data class WeatherWidget(
        val location: String,
        val currentTemp: Int,
        val condition: WeatherCondition,
        val highTemp: Int,
        val lowTemp: Int
    ) : HomeWidget()

    data class NewsWidget(
        val title: String,
        val newsCount: Int,
        val latestNews: List<NewsPreview>
    ) : HomeWidget()

    data class AdsWidget(
        val title: String,
        val adsCount: Int,
        val latestAds: List<Ad>
    ) : HomeWidget()

    data class ProfileWidget(
        val user: User?
    ) : HomeWidget()

    data class QuickActionsWidget(
        val actions: List<QuickAction>
    ) : HomeWidget()

    data class DeliveryWidget(
        val title: String,
        val deliveryCount: Int,
        val latestDeliveries: List<Delivery>
    ) : HomeWidget()

    data class TasksWidget(
        val title: String,
        val taskCount: Int,
        val latestTasks: List<Task>
    ) : HomeWidget()

    data class AdminWidget(
        val deliveryName: String,
        val todayOrders: Int,
        val todayRevenue: String,
        val isOpen: Boolean
    ) : HomeWidget()

    data class EmergencyWidget(
        val title: String,
        val mainNumbers: List<String>
    ) : HomeWidget()

    data class ComplaintWidget(
        val title: String,
        val subtitle: String
    ) : HomeWidget()

    data class NotificationsWidget(
        val unreadCount: Int,
        val latestTypes: List<String>
    ) : HomeWidget()

    data class ChatWidget(
        val onlineCount: Int,
        val membersCount: Int
    ) : HomeWidget()

    data class CinemaWidget(
        val nowPlayingCount: Int,
        val cinemasCount: Int,
        val upcomingMovies: List<String>
    ) : HomeWidget()

    data class CurrencyWidget(
        val usdRate: Double?,
        val eurRate: Double?,
        val cnyRate: Double?,
        val jpyRate: Double?,
        val lastUpdate: String
    ) : HomeWidget()

    data class CompanyWidget(
        val totalCount: Int,
        val verifiedCount: Int,
        val categoriesCount: Int
    ) : HomeWidget()

    data class HoroscopeWidget(
        val state: HoroscopeWidgetState
    ) : HomeWidget()
}

sealed class HoroscopeWidgetState {
    data class Ready(
        val zodiacSign: String,
        val zodiacSignLabel: String,
        val symbol: String,
        val date: String?,
        val text: String
    ) : HoroscopeWidgetState()

    object Anonymous : HoroscopeWidgetState()
    object NoBirthday : HoroscopeWidgetState()
    object Empty : HoroscopeWidgetState()
}

data class QuickAction(
    val id: String,
    val title: String,
    val icon: Int
)

data class NewsPreview(
    val id: Int?,
    val title: String
)

object MockHomeWidgets {

    fun getLocalWidgets(): Map<String, HomeWidget> {
        val ads = MockAds.getAll()
        val deliveries = MockDeliveries.getAll()
        val tasks = MockTasks.getTasks()
        val admin = MockDeliveryAdmin.getDeliveryAdmin()

        return mapOf(
            "notifications" to HomeWidget.NotificationsWidget(
                unreadCount = MockNotifications.getUnreadCount(),
                latestTypes = listOf("⚠️", "🍕", "🎉")
            ),
            "chat" to HomeWidget.ChatWidget(
                onlineCount = MockChat.getOnlineCount(),
                membersCount = MockChat.getMembersCount()
            ),
            "cinema" to HomeWidget.CinemaWidget(
                nowPlayingCount = MockCinemas.getNowPlayingCount(),
                cinemasCount = MockCinemas.getCinemas().size,
                upcomingMovies = MockCinemas.getUpcomingMovies()
            ),
            "emergency" to HomeWidget.EmergencyWidget(
                title = "Экстренная помощь",
                mainNumbers = listOf("112", "101", "102", "103")
            ),
            "complaint" to HomeWidget.ComplaintWidget(
                title = "Обращения",
                subtitle = "Сообщите о проблеме в городе"
            ),
            "admin" to HomeWidget.AdminWidget(
                deliveryName = admin.delivery.name,
                todayOrders = 12,
                todayRevenue = "15 600 ₽",
                isOpen = admin.isOpen
            ),
            "tasks" to HomeWidget.TasksWidget(
                title = "Задания",
                taskCount = tasks.size,
                latestTasks = tasks.take(3)
            ),
            "delivery" to HomeWidget.DeliveryWidget(
                title = "Доставка еды",
                deliveryCount = deliveries.size,
                latestDeliveries = deliveries.take(3)
            ),
            "announcements" to HomeWidget.AdsWidget(
                title = "Новые объявления",
                adsCount = ads.size,
                latestAds = ads.take(3)
            )
        )
    }
}
