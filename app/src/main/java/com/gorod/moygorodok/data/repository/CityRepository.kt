package com.gorod.moygorodok.data.repository

import android.content.Context
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.remote.ApiClient
import com.gorod.moygorodok.data.remote.model.CitiesResponse
import com.gorod.moygorodok.data.remote.model.City
import com.gorod.moygorodok.data.remote.model.CityOverviewData
import com.gorod.moygorodok.data.remote.model.CityOverviewResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

class CityRepository private constructor(context: Context) {

    private val api = ApiClient.apiService
    private val cityManager = CityManager.getInstance(context)
    private val gson = Gson()

    suspend fun getOverview(forceRefresh: Boolean = false): Result<CityOverviewData> {
        if (!forceRefresh) {
            val cached = readOverviewCacheIfFresh()
            if (cached != null) return Result.success(cached)
        }
        return try {
            val response = api.getCitiesOverview()
            if (response.success) {
                cityManager.saveOverviewCache(gson.toJson(response))
                Result.success(response.data)
            } else {
                val cached = readOverviewCacheAny()
                if (cached != null) Result.success(cached)
                else Result.failure(Exception("Не удалось загрузить города"))
            }
        } catch (e: Exception) {
            val cached = readOverviewCacheAny()
            if (cached != null) Result.success(cached)
            else Result.failure(e)
        }
    }

    suspend fun search(query: String): Result<List<City>> {
        if (query.isBlank()) return Result.success(emptyList())
        return wrapList { api.searchCities(query) }
    }

    suspend fun nearby(lat: Double, lng: Double, radius: Int = 50): Result<List<City>> {
        return wrapList { api.nearbyCities(lat, lng, radius) }
    }

    suspend fun getRegions(): Result<List<City>> {
        return try {
            val response = api.getRegions()
            if (response.success) Result.success(response.data)
            else Result.failure(Exception("Ошибка загрузки регионов"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSettlements(districtId: Int): Result<List<City>> {
        return wrapList { api.getSettlements(districtId) }
    }

    suspend fun getDistricts(regionId: Int): Result<List<City>> {
        return wrapList { api.getDistricts(regionId) }
    }

    private suspend fun wrapList(call: suspend () -> CitiesResponse): Result<List<City>> {
        return try {
            val response = call()
            if (response.success) Result.success(response.data)
            else Result.failure(Exception("Ошибка сервера"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun readOverviewCacheIfFresh(): CityOverviewData? {
        val at = cityManager.cachedOverviewAt.first() ?: return null
        val age = System.currentTimeMillis() - at
        if (age > CACHE_TTL_MS) return null
        return readOverviewCacheAny()
    }

    private suspend fun readOverviewCacheAny(): CityOverviewData? {
        val json = cityManager.cachedOverviewJson.first() ?: return null
        return try {
            gson.fromJson(json, CityOverviewResponse::class.java)?.data
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val CACHE_TTL_MS = 24L * 60 * 60 * 1000

        @Volatile
        private var instance: CityRepository? = null

        fun getInstance(context: Context): CityRepository {
            return instance ?: synchronized(this) {
                instance ?: CityRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
