package com.gorod.moygorodok.data.remote.model

import com.google.gson.annotations.SerializedName

// ---------- List item ----------

data class CompanyDto(
    val id: Int,
    val name: String,
    val slug: String? = null,
    val kind: String,
    @SerializedName("kind_label") val kindLabel: String,
    val category: CompanyCategoryDto? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val rating: Float? = null,
    @SerializedName("reviews_count") val reviewsCount: Int = 0,
    @SerializedName("price_range") val priceRange: PriceRangeDto? = null,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("is_open_now") val isOpenNow: Boolean? = null,
    @SerializedName("logo_url") val logoUrl: String? = null,
    @SerializedName("cover_photo_url") val coverPhotoUrl: String? = null,
    @SerializedName("city_id") val cityId: Int
)

data class CompanyCategoryDto(
    val id: Int,
    val key: String,
    val name: String,
    @SerializedName("icon_key") val iconKey: String? = null,
    @SerializedName("color_hex") val colorHex: String? = null
)

data class PriceRangeDto(
    val key: String,
    val symbol: String
)

// ---------- Detail ----------

data class CompanyDetailDto(
    val id: Int,
    val name: String,
    val slug: String? = null,
    val kind: String,
    @SerializedName("kind_label") val kindLabel: String,
    val category: CompanyCategoryDto? = null,
    val description: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val services: List<String> = emptyList(),
    @SerializedName("price_range") val priceRange: PriceRangeDto? = null,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("working_hours") val workingHours: WorkingHoursDto? = null,
    @SerializedName("logo_url") val logoUrl: String? = null,
    val rating: Float? = null,
    @SerializedName("reviews_count") val reviewsCount: Int = 0,
    @SerializedName("cover_photo_url") val coverPhotoUrl: String? = null,
    val photos: List<CompanyPhotoDto> = emptyList(),
    @SerializedName("recent_reviews") val recentReviews: List<CompanyReviewDto> = emptyList(),
    val city: CityMini? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class WorkingHoursDto(
    val schedule: List<WorkingDayDto> = emptyList(),
    @SerializedName("is_open_now") val isOpenNow: Boolean? = null,
    @SerializedName("today_label") val todayLabel: String? = null,
    @SerializedName("today_day_of_week") val todayDayOfWeek: Int? = null
)

data class WorkingDayDto(
    @SerializedName("day_of_week") val dayOfWeek: Int,
    @SerializedName("is_closed") val isClosed: Boolean = false,
    val intervals: List<TimeIntervalDto> = emptyList()
)

data class TimeIntervalDto(
    @SerializedName("opens_at") val opensAt: String,
    @SerializedName("closes_at") val closesAt: String
)

// ---------- Reviews ----------

data class CompanyReviewDto(
    val id: Int,
    val rating: Int,
    val text: String? = null,
    @SerializedName("published_at") val publishedAt: String? = null,
    val user: CompanyReviewUserDto? = null,
    val photos: List<ReviewPhotoDto> = emptyList()
)

data class CompanyReviewUserDto(
    val id: Int,
    val name: String,
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

// ---------- Photos ----------

data class CompanyPhotoDto(
    val id: Int,
    val url: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    @SerializedName("sort_order") val sortOrder: Int = 0,
    val status: String? = null
)

data class ReviewPhotoDto(
    val id: Int,
    val url: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val status: String? = null
)

// ---------- Requests ----------

data class CreateReviewRequest(
    val rating: Int,
    val text: String? = null
)

// ---------- Response envelopes ----------

data class CompanyListResponse(
    val success: Boolean,
    val data: List<CompanyDto> = emptyList(),
    val meta: PaginationMeta? = null,
    val message: String? = null
)

data class CompanyDetailResponse(
    val success: Boolean,
    val data: CompanyDetailDto? = null,
    val message: String? = null
)

data class CompanyCategoriesResponse(
    val success: Boolean,
    val data: List<CompanyCategoryDto> = emptyList(),
    val message: String? = null
)

data class CompanyReviewListResponse(
    val success: Boolean,
    val data: List<CompanyReviewDto> = emptyList(),
    val meta: PaginationMeta? = null,
    val message: String? = null
)

data class CompanyReviewResponse(
    val success: Boolean,
    val data: CompanyReviewDto? = null,
    val message: String? = null
)

data class CompanyPhotoResponse(
    val success: Boolean,
    val data: CompanyPhotoDto? = null,
    val message: String? = null
)

data class ReviewPhotoResponse(
    val success: Boolean,
    val data: ReviewPhotoDto? = null,
    val message: String? = null
)
