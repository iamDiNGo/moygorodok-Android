package com.gorod.moygorodok.data.model

data class Weather(
    val location: String,
    val currentTemp: Int,
    val feelsLike: Int?,
    val condition: WeatherCondition,
    val description: String?,
    val highTemp: Int?,
    val lowTemp: Int?,
    val humidity: Int?,
    val windSpeed: Double?,
    val windDirection: String?,
    val pressure: Int?,
    val sunrise: String?,
    val sunset: String?,
    val hourlyForecast: List<HourlyWeather>,
    val dailyForecast: List<DailyWeather>
)

data class HourlyWeather(
    val hour: String,
    val temp: Int,
    val condition: WeatherCondition,
    val precipProbability: Int
)

data class DailyWeather(
    val dayOfWeek: String,
    val date: String,
    val highTemp: Int,
    val lowTemp: Int,
    val condition: WeatherCondition,
    val precipProbability: Int
)

enum class WeatherCondition(val displayName: String, val icon: String) {
    SUNNY("Ясно", "☀️"),
    PARTLY_CLOUDY("Переменная облачность", "⛅"),
    CLOUDY("Облачно", "☁️"),
    OVERCAST("Пасмурно", "🌥️"),
    RAIN("Дождь", "🌧️"),
    LIGHT_RAIN("Небольшой дождь", "🌦️"),
    THUNDERSTORM("Гроза", "⛈️"),
    SNOW("Снег", "🌨️"),
    FOG("Туман", "🌫️"),
    WIND("Ветрено", "💨")
}
