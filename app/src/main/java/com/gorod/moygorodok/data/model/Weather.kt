package com.gorod.moygorodok.data.model

import java.time.LocalDateTime

data class Weather(
    val location: String,
    val currentTemp: Int,
    val feelsLike: Int,
    val condition: WeatherCondition,
    val description: String,
    val highTemp: Int,
    val lowTemp: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: String,
    val pressure: Int,
    val visibility: Double,
    val uvIndex: Int,
    val sunrise: String,
    val sunset: String,
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

object MockWeather {

    fun getCurrentWeather(): Weather {
        return Weather(
            location = "Мой Городок",
            currentTemp = -5,
            feelsLike = -9,
            condition = WeatherCondition.PARTLY_CLOUDY,
            description = "Переменная облачность, временами снег",
            highTemp = -2,
            lowTemp = -12,
            humidity = 78,
            windSpeed = 4.5,
            windDirection = "СЗ",
            pressure = 745,
            visibility = 8.0,
            uvIndex = 1,
            sunrise = "08:32",
            sunset = "16:45",
            hourlyForecast = getHourlyForecast(),
            dailyForecast = getDailyForecast()
        )
    }

    private fun getHourlyForecast(): List<HourlyWeather> {
        return listOf(
            HourlyWeather("Сейчас", -5, WeatherCondition.PARTLY_CLOUDY, 10),
            HourlyWeather("13:00", -4, WeatherCondition.CLOUDY, 20),
            HourlyWeather("14:00", -3, WeatherCondition.CLOUDY, 30),
            HourlyWeather("15:00", -2, WeatherCondition.LIGHT_RAIN, 60),
            HourlyWeather("16:00", -3, WeatherCondition.SNOW, 80),
            HourlyWeather("17:00", -4, WeatherCondition.SNOW, 70),
            HourlyWeather("18:00", -5, WeatherCondition.CLOUDY, 40),
            HourlyWeather("19:00", -6, WeatherCondition.PARTLY_CLOUDY, 20),
            HourlyWeather("20:00", -7, WeatherCondition.PARTLY_CLOUDY, 10),
            HourlyWeather("21:00", -8, WeatherCondition.CLOUDY, 15),
            HourlyWeather("22:00", -9, WeatherCondition.CLOUDY, 20),
            HourlyWeather("23:00", -10, WeatherCondition.PARTLY_CLOUDY, 10)
        )
    }

    private fun getDailyForecast(): List<DailyWeather> {
        return listOf(
            DailyWeather("Сегодня", "18 ноя", -2, -12, WeatherCondition.PARTLY_CLOUDY, 30),
            DailyWeather("Вт", "19 ноя", -1, -8, WeatherCondition.CLOUDY, 40),
            DailyWeather("Ср", "20 ноя", 0, -6, WeatherCondition.LIGHT_RAIN, 70),
            DailyWeather("Чт", "21 ноя", -3, -10, WeatherCondition.SNOW, 80),
            DailyWeather("Пт", "22 ноя", -5, -14, WeatherCondition.SUNNY, 5),
            DailyWeather("Сб", "23 ноя", -4, -12, WeatherCondition.PARTLY_CLOUDY, 15),
            DailyWeather("Вс", "24 ноя", -2, -9, WeatherCondition.CLOUDY, 25),
            DailyWeather("Пн", "25 ноя", -1, -7, WeatherCondition.PARTLY_CLOUDY, 20),
            DailyWeather("Вт", "26 ноя", 1, -5, WeatherCondition.CLOUDY, 35),
            DailyWeather("Ср", "27 ноя", 2, -3, WeatherCondition.LIGHT_RAIN, 60)
        )
    }
}
