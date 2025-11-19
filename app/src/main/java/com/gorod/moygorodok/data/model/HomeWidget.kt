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
        val latestNews: List<News>
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
        val usdRate: Double,
        val eurRate: Double,
        val cnyRate: Double,
        val jpyRate: Double,
        val lastUpdate: String
    ) : HomeWidget()

    data class CompanyWidget(
        val totalCount: Int,
        val verifiedCount: Int,
        val categoriesCount: Int
    ) : HomeWidget()
}

data class QuickAction(
    val id: String,
    val title: String,
    val icon: Int
)

object MockHomeWidgets {

    fun getWidgets(): List<HomeWidget> {
        val weather = MockWeather.getCurrentWeather()
        val news = MockNews.getAll()
        val ads = MockAds.getAll()
        val deliveries = MockDeliveries.getAll()
        val tasks = MockTasks.getTasks()
        val admin = MockDeliveryAdmin.getDeliveryAdmin()

        return listOf(
            HomeWidget.NotificationsWidget(
                unreadCount = MockNotifications.getUnreadCount(),
                latestTypes = listOf("⚠️", "🍕", "🎉")
            ),
            HomeWidget.ChatWidget(
                onlineCount = MockChat.getOnlineCount(),
                membersCount = MockChat.getMembersCount()
            ),
            HomeWidget.CinemaWidget(
                nowPlayingCount = MockCinemas.getNowPlayingCount(),
                cinemasCount = MockCinemas.getCinemas().size,
                upcomingMovies = MockCinemas.getUpcomingMovies()
            ),
            HomeWidget.CurrencyWidget(
                usdRate = MockCurrencies.getCurrencyByCode("USD")?.cbRate ?: 92.35,
                eurRate = MockCurrencies.getCurrencyByCode("EUR")?.cbRate ?: 100.65,
                cnyRate = MockCurrencies.getCurrencyByCode("CNY")?.cbRate ?: 12.85,
                jpyRate = MockCurrencies.getCurrencyByCode("JPY")?.cbRate ?: 0.625,
                lastUpdate = MockCurrencies.getLastUpdate()
            ),
            HomeWidget.CompanyWidget(
                totalCount = MockCompanies.getTotalCount(),
                verifiedCount = MockCompanies.getVerifiedCount(),
                categoriesCount = MockCompanies.getCategories().size
            ),
            HomeWidget.EmergencyWidget(
                title = "Экстренная помощь",
                mainNumbers = listOf("112", "101", "102", "103")
            ),
            HomeWidget.ComplaintWidget(
                title = "Обращения",
                subtitle = "Сообщите о проблеме в городе"
            ),
            HomeWidget.AdminWidget(
                deliveryName = admin.delivery.name,
                todayOrders = 12,
                todayRevenue = "15 600 ₽",
                isOpen = admin.isOpen
            ),
            HomeWidget.WeatherWidget(
                location = weather.location,
                currentTemp = weather.currentTemp,
                condition = weather.condition,
                highTemp = weather.highTemp,
                lowTemp = weather.lowTemp
            ),
            HomeWidget.TasksWidget(
                title = "Задания",
                taskCount = tasks.size,
                latestTasks = tasks.take(3)
            ),
            HomeWidget.DeliveryWidget(
                title = "Доставка еды",
                deliveryCount = deliveries.size,
                latestDeliveries = deliveries.take(3)
            ),
            HomeWidget.NewsWidget(
                title = "Последние новости",
                newsCount = news.size,
                latestNews = news.take(3)
            ),
            HomeWidget.AdsWidget(
                title = "Новые объявления",
                adsCount = ads.size,
                latestAds = ads.take(3)
            )
        )
    }
}
