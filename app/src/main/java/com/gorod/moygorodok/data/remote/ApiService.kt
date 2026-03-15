package com.gorod.moygorodok.data.remote

import com.gorod.moygorodok.data.model.User
import com.gorod.moygorodok.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/home")
    suspend fun getHome(): HomeResponse

    // Auth
    @POST("api/auth/send-code")
    suspend fun sendCode(@Body request: SendCodeRequest): Response<ApiResponse<SendCodeData>>

    @POST("api/auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<ApiResponse<AuthData>>

    @Multipart
    @POST("api/auth/register")
    suspend fun register(
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("code") code: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part avatar: MultipartBody.Part? = null
    ): Response<ApiResponse<AuthData>>

    @GET("api/auth/me")
    suspend fun getProfile(): Response<ApiResponse<User>>

    @Multipart
    @POST("api/auth/profile")
    suspend fun updateProfile(
        @Part("_method") method: RequestBody,
        @Part("name") name: RequestBody? = null,
        @Part("email") email: RequestBody? = null,
        @Part("gender") gender: RequestBody? = null,
        @Part avatar: MultipartBody.Part? = null
    ): Response<ApiResponse<User>>

    @DELETE("api/auth/avatar")
    suspend fun deleteAvatar(): Response<ApiResponse<User>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    @POST("api/auth/logout-all")
    suspend fun logoutAll(): Response<ApiResponse<Any>>
}
