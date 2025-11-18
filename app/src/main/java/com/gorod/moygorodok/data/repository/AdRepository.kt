package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.Ad
import com.gorod.moygorodok.data.model.AdFilter
import com.gorod.moygorodok.data.model.MockAds
import kotlinx.coroutines.delay

class AdRepository {

    suspend fun getAds(filter: AdFilter = AdFilter()): Result<List<Ad>> {
        delay(800)
        return Result.success(MockAds.getFiltered(filter))
    }

    suspend fun getAdById(id: String): Result<Ad> {
        delay(500)
        val ad = MockAds.getById(id)
        return if (ad != null) {
            Result.success(ad)
        } else {
            Result.failure(Exception("Объявление не найдено"))
        }
    }

    suspend fun refreshAds(filter: AdFilter = AdFilter()): Result<List<Ad>> {
        delay(1000)
        return Result.success(MockAds.getFiltered(filter))
    }

    suspend fun toggleFavorite(adId: String): Result<Boolean> {
        delay(300)
        // В реальном приложении здесь была бы логика сохранения
        return Result.success(true)
    }

    companion object {
        @Volatile
        private var instance: AdRepository? = null

        fun getInstance(): AdRepository {
            return instance ?: synchronized(this) {
                instance ?: AdRepository().also { instance = it }
            }
        }
    }
}
