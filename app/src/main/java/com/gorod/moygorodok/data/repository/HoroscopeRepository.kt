package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.remote.ApiClient
import com.gorod.moygorodok.data.remote.model.HoroscopeBundleDto

class HoroscopeRepository private constructor() {

    private val api = ApiClient.apiService

    /**
     * Все периоды знака за один запрос (today/tomorrow/weekly/monthly). Контракт API:
     *  - 200 + data != null  → бандл получен (отдельные периоды внутри могут быть null)
     *  - 200 + data == null  → знак валиден, но данных нет вовсе
     *  - прочее (4xx/5xx/сеть) → ошибка (Result.failure)
     */
    suspend fun getBundle(sign: String): Result<HoroscopeBundleDto?> {
        return try {
            val response = api.getHoroscopeBundleBySign(sign)
            val body = response.body()
            when {
                response.isSuccessful && body?.success == true -> Result.success(body.data)
                else -> Result.failure(Exception(body?.message ?: "Ошибка сервера"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    companion object {
        @Volatile
        private var instance: HoroscopeRepository? = null

        fun getInstance(): HoroscopeRepository {
            return instance ?: synchronized(this) {
                instance ?: HoroscopeRepository().also { instance = it }
            }
        }
    }
}
