package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.MockWeather
import com.gorod.moygorodok.data.model.Weather
import kotlinx.coroutines.delay

class WeatherRepository {

    suspend fun getCurrentWeather(): Weather {
        delay(500) // Simulate network delay
        return MockWeather.getCurrentWeather()
    }

    suspend fun refreshWeather(): Weather {
        delay(1000) // Simulate longer refresh
        return MockWeather.getCurrentWeather()
    }
}
