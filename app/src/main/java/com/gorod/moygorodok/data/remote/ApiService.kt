package com.gorod.moygorodok.data.remote

import com.gorod.moygorodok.data.remote.model.HomeResponse
import retrofit2.http.GET

interface ApiService {

    @GET("api/home")
    suspend fun getHome(): HomeResponse
}
