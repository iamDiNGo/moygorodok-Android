package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.remote.ApiClient
import com.gorod.moygorodok.data.remote.model.HomeCellDto

class HomeRepository private constructor() {

    private val apiService = ApiClient.apiService

    suspend fun getHomeCells(cityId: Int?): Result<List<HomeCellDto>> {
        return try {
            val response = apiService.getHome(cityId)
            if (response.success) {
                Result.success(response.data.sortedBy { it.sortOrder })
            } else {
                Result.failure(Exception("Server returned success=false"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var instance: HomeRepository? = null

        fun getInstance(): HomeRepository {
            return instance ?: synchronized(this) {
                instance ?: HomeRepository().also { instance = it }
            }
        }
    }
}
