package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.remote.ApiClient
import com.gorod.moygorodok.data.remote.model.HoroscopeDataDto

class HoroscopeRepository private constructor() {

    private val api = ApiClient.apiService

    suspend fun getBySign(sign: String): Result<HoroscopeDataDto> {
        return try {
            val response = api.getHoroscopeBySign(sign)
            val body = response.body()
            when {
                response.isSuccessful && body?.success == true && body.data != null -> Result.success(body.data)
                response.code() == 404 -> Result.failure(HoroscopeNotFoundException(sign))
                else -> Result.failure(Exception(body?.message ?: "Ошибка сервера"))
            }
        } catch (e: Exception) {
            if (e is HoroscopeNotFoundException) throw e
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

class HoroscopeNotFoundException(val sign: String) : Exception("Гороскоп для знака $sign не найден")
