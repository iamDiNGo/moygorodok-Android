package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.remote.ApiClient
import com.gorod.moygorodok.data.remote.model.HoroscopeDataDto

class HoroscopeRepository private constructor() {

    private val api = ApiClient.apiService

    /**
     * Гороскоп для знака. Контракт API:
     *  - 200 + data != null  → прогноз есть (Result.success(data))
     *  - 200 + data == null  → прогноза на дату ещё нет (Result.success(null))
     *  - прочее (4xx/5xx/сеть) → ошибка (Result.failure)
     */
    suspend fun getBySign(sign: String): Result<HoroscopeDataDto?> {
        return try {
            val response = api.getHoroscopeBySign(sign)
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
