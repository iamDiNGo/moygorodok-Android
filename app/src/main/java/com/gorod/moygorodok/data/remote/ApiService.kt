package com.gorod.moygorodok.data.remote

import com.gorod.moygorodok.data.model.Report
import com.gorod.moygorodok.data.model.ReportPhoto
import com.gorod.moygorodok.data.model.User
import com.gorod.moygorodok.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/home")
    suspend fun getHome(
        @Query("city_id") cityId: Int? = null
    ): HomeResponse

    // Cities
    @GET("api/cities/overview")
    suspend fun getCitiesOverview(): CityOverviewResponse

    @GET("api/cities/search/{query}")
    suspend fun searchCities(
        @Path("query") query: String,
        @Query("per_page") perPage: Int = 30
    ): CitiesResponse

    @GET("api/cities/nearby")
    suspend fun nearbyCities(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Int = 50,
        @Query("per_page") perPage: Int = 20
    ): CitiesResponse

    @GET("api/cities/regions")
    suspend fun getRegions(): RegionsResponse

    @GET("api/cities/{id}/settlements")
    suspend fun getSettlements(@Path("id") id: Int): CitiesResponse

    @GET("api/cities/{id}/districts")
    suspend fun getDistricts(@Path("id") id: Int): CitiesResponse

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
        @Part("city_id") cityId: RequestBody? = null,
        @Part("birthday") birthday: RequestBody? = null,
        @Part avatar: MultipartBody.Part? = null
    ): Response<ApiResponse<User>>

    @DELETE("api/auth/avatar")
    suspend fun deleteAvatar(): Response<ApiResponse<User>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    @POST("api/auth/logout-all")
    suspend fun logoutAll(): Response<ApiResponse<Any>>

    // Weather
    @GET("api/weather/city/{city}")
    suspend fun getWeatherForCity(
        @Path("city") cityId: Int
    ): Response<ApiResponse<WeatherCellDataDto>>

    // News
    @GET("api/news")
    suspend fun getNewsList(
        @Query("city_id") cityId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<NewsListResponse>

    @GET("api/news/{id}")
    suspend fun getNewsById(
        @Path("id") id: Int
    ): Response<ApiResponse<NewsDetailDto>>

    // Horoscope
    @GET("api/horoscope/{sign}")
    suspend fun getHoroscopeBySign(
        @Path("sign") sign: String
    ): Response<ApiResponse<HoroscopeDataDto>>

    @GET("api/horoscope/me")
    suspend fun getMyHoroscope(): Response<ApiResponse<HoroscopeDataDto>>

    // Все периоды одного знака за один запрос (today/tomorrow/weekly/monthly).
    @GET("api/horoscope/{sign}/all")
    suspend fun getHoroscopeBundleBySign(
        @Path("sign") sign: String
    ): Response<ApiResponse<HoroscopeBundleDto>>

    // Emergency
    @GET("api/emergency-contacts")
    suspend fun getEmergencyContacts(
        @Query("city_id") cityId: Int? = null
    ): Response<EmergencyListResponse>

    // Reports
    @POST("api/reports")
    suspend fun createReport(
        @Body request: CreateReportRequest
    ): Response<ApiResponse<Report>>

    @Multipart
    @POST("api/reports/{id}/photos")
    suspend fun uploadReportPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<ApiResponse<ReportPhoto>>

    @GET("api/reports/my")
    suspend fun getMyReports(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<ReportListResponse>

    @GET("api/reports/{id}")
    suspend fun getReport(@Path("id") id: Int): Response<ApiResponse<Report>>

    @DELETE("api/reports/{id}")
    suspend fun deleteReport(@Path("id") id: Int): Response<ApiResponse<Any>>

    // Announcements
    @GET("api/announcements")
    suspend fun getAnnouncements(
        @Query("city_id") cityId: Int? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<AnnouncementListResponse>

    @GET("api/announcements/my")
    suspend fun getMyAnnouncements(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<AnnouncementListResponse>

    @GET("api/announcements/favorites")
    suspend fun getFavoriteAnnouncements(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<AnnouncementListResponse>

    @GET("api/announcements/{id}")
    suspend fun getAnnouncement(
        @Path("id") id: Int
    ): Response<AnnouncementDetailResponse>

    @POST("api/announcements")
    suspend fun createAnnouncement(
        @Body request: CreateAnnouncementRequest
    ): Response<AnnouncementDetailResponse>

    @PUT("api/announcements/{id}")
    suspend fun updateAnnouncement(
        @Path("id") id: Int,
        @Body request: UpdateAnnouncementRequest
    ): Response<AnnouncementDetailResponse>

    @DELETE("api/announcements/{id}")
    suspend fun deleteAnnouncement(
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>

    @PATCH("api/announcements/{id}/close")
    suspend fun closeAnnouncement(
        @Path("id") id: Int
    ): Response<AnnouncementDetailResponse>

    @PATCH("api/announcements/{id}/renew")
    suspend fun renewAnnouncement(
        @Path("id") id: Int
    ): Response<AnnouncementDetailResponse>

    @Multipart
    @POST("api/announcements/{id}/photos")
    suspend fun uploadAnnouncementPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<AnnouncementPhotoResponse>

    @DELETE("api/announcements/{id}/photos/{photoId}")
    suspend fun deleteAnnouncementPhoto(
        @Path("id") id: Int,
        @Path("photoId") photoId: Int
    ): Response<ApiResponse<Any>>

    @POST("api/announcements/{id}/favorite")
    suspend fun addAnnouncementFavorite(
        @Path("id") id: Int
    ): Response<FavoriteToggleResponse>

    @DELETE("api/announcements/{id}/favorite")
    suspend fun removeAnnouncementFavorite(
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>

    // Companies
    @GET("api/cities/{cityId}/companies")
    suspend fun getCompanies(
        @Path("cityId") cityId: Int,
        @Query("search") search: String? = null,
        @Query("kind") kind: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("open_now") openNow: Int? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<CompanyListResponse>

    @GET("api/companies/{id}")
    suspend fun getCompany(
        @Path("id") id: Int
    ): Response<CompanyDetailResponse>

    @GET("api/company-categories")
    suspend fun getCompanyCategories(): Response<CompanyCategoriesResponse>

    @GET("api/companies/{id}/reviews")
    suspend fun getCompanyReviews(
        @Path("id") id: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<CompanyReviewListResponse>

    @POST("api/companies/{id}/reviews")
    suspend fun createCompanyReview(
        @Path("id") id: Int,
        @Body body: CreateReviewRequest
    ): Response<CompanyReviewResponse>

    @PUT("api/companies/{id}/reviews/me")
    suspend fun updateMyCompanyReview(
        @Path("id") id: Int,
        @Body body: CreateReviewRequest
    ): Response<CompanyReviewResponse>

    @DELETE("api/companies/{id}/reviews/me")
    suspend fun deleteMyCompanyReview(
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>

    @Multipart
    @POST("api/companies/{id}/photos")
    suspend fun uploadCompanyPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<CompanyPhotoResponse>

    @Multipart
    @POST("api/companies/{id}/reviews/me/photos")
    suspend fun uploadCompanyReviewPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<ReviewPhotoResponse>

    @DELETE("api/companies/{id}/reviews/me/photos/{photoId}")
    suspend fun deleteCompanyReviewPhoto(
        @Path("id") id: Int,
        @Path("photoId") photoId: Int
    ): Response<ApiResponse<Any>>
}
