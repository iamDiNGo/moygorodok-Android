package com.gorod.moygorodok.data.model

import com.gorod.moygorodok.data.remote.model.CompanyCategoryDto
import com.gorod.moygorodok.data.remote.model.CompanyDetailDto
import com.gorod.moygorodok.data.remote.model.CompanyDto
import com.gorod.moygorodok.data.remote.model.CompanyPhotoDto
import com.gorod.moygorodok.data.remote.model.CompanyReviewDto
import com.gorod.moygorodok.data.remote.model.PriceRangeDto
import com.gorod.moygorodok.data.remote.model.ReviewPhotoDto
import com.gorod.moygorodok.data.remote.model.TimeIntervalDto
import com.gorod.moygorodok.data.remote.model.WorkingDayDto
import com.gorod.moygorodok.data.remote.model.WorkingHoursDto
import com.gorod.moygorodok.util.IsoDateParser
import java.util.Date

/**
 * Краткое представление заведения для списков.
 */
data class Company(
    val id: Int,
    val name: String,
    val slug: String?,
    val kind: CompanyKind,
    val kindLabel: String,
    val category: CompanyCategory?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val rating: Float?,
    val reviewsCount: Int,
    val priceRange: PriceRange?,
    val isVerified: Boolean,
    val isOpenNow: Boolean?,
    val logoUrl: String?,
    val coverPhotoUrl: String?,
    val cityId: Int
)

/**
 * Полное представление заведения для карточки.
 */
data class CompanyDetail(
    val id: Int,
    val name: String,
    val slug: String?,
    val kind: CompanyKind,
    val kindLabel: String,
    val category: CompanyCategory?,
    val description: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val email: String?,
    val website: String?,
    val services: List<String>,
    val priceRange: PriceRange?,
    val isVerified: Boolean,
    val workingHours: WorkingHours?,
    val logoUrl: String?,
    val rating: Float?,
    val reviewsCount: Int,
    val coverPhotoUrl: String?,
    val photos: List<CompanyPhoto>,
    val recentReviews: List<CompanyReview>,
    val cityId: Int?,
    val cityName: String?,
    val createdAt: Date?,
    val updatedAt: Date?
)

data class CompanyCategory(
    val id: Int,
    val key: String,
    val name: String,
    val iconKey: String?,
    val colorHex: String?
)

data class PriceRange(val key: String, val symbol: String)

enum class CompanyKind(val apiValue: String) {
    COMMERCIAL("commercial"),
    GOVERNMENT("government"),
    NONPROFIT("nonprofit"),
    UNKNOWN("");

    companion object {
        fun fromApi(value: String?): CompanyKind =
            entries.firstOrNull { it.apiValue == value } ?: UNKNOWN
    }
}

data class WorkingHours(
    val schedule: List<WorkingDay>,
    val isOpenNow: Boolean?,
    val todayLabel: String?,
    val todayDayOfWeek: Int?
)

data class WorkingDay(
    val dayOfWeek: Int,
    val isClosed: Boolean,
    val intervals: List<TimeInterval>
)

data class TimeInterval(val opensAt: String, val closesAt: String)

data class CompanyPhoto(
    val id: Int,
    val url: String,
    val thumbnailUrl: String?,
    val width: Int?,
    val height: Int?,
    val sortOrder: Int,
    val status: String?
)

data class CompanyReview(
    val id: Int,
    val rating: Int,
    val text: String?,
    val publishedAt: Date?,
    val author: CompanyReviewAuthor?,
    val photos: List<ReviewPhoto>
)

data class CompanyReviewAuthor(
    val id: Int,
    val name: String,
    val avatarUrl: String?
)

data class ReviewPhoto(
    val id: Int,
    val url: String,
    val thumbnailUrl: String?,
    val width: Int?,
    val height: Int?,
    val status: String?
)

// ---------- DTO → Domain mappers ----------

fun CompanyDto.toDomain(): Company = Company(
    id = id,
    name = name,
    slug = slug,
    kind = CompanyKind.fromApi(kind),
    kindLabel = kindLabel,
    category = category?.toDomain(),
    address = address,
    latitude = latitude,
    longitude = longitude,
    phone = phone,
    rating = rating,
    reviewsCount = reviewsCount,
    priceRange = priceRange?.toDomain(),
    isVerified = isVerified,
    isOpenNow = isOpenNow,
    logoUrl = logoUrl,
    coverPhotoUrl = coverPhotoUrl,
    cityId = cityId
)

fun CompanyDetailDto.toDomain(): CompanyDetail = CompanyDetail(
    id = id,
    name = name,
    slug = slug,
    kind = CompanyKind.fromApi(kind),
    kindLabel = kindLabel,
    category = category?.toDomain(),
    description = description,
    address = address,
    latitude = latitude,
    longitude = longitude,
    phone = phone,
    email = email,
    website = website,
    services = services,
    priceRange = priceRange?.toDomain(),
    isVerified = isVerified,
    workingHours = workingHours?.toDomain(),
    logoUrl = logoUrl,
    rating = rating,
    reviewsCount = reviewsCount,
    coverPhotoUrl = coverPhotoUrl,
    photos = photos.map { it.toDomain() },
    recentReviews = recentReviews.map { it.toDomain() },
    cityId = city?.id,
    cityName = city?.name,
    createdAt = IsoDateParser.parse(createdAt),
    updatedAt = IsoDateParser.parse(updatedAt)
)

fun CompanyCategoryDto.toDomain(): CompanyCategory =
    CompanyCategory(id = id, key = key, name = name, iconKey = iconKey, colorHex = colorHex)

fun PriceRangeDto.toDomain(): PriceRange = PriceRange(key = key, symbol = symbol)

fun WorkingHoursDto.toDomain(): WorkingHours = WorkingHours(
    schedule = schedule.map { it.toDomain() },
    isOpenNow = isOpenNow,
    todayLabel = todayLabel,
    todayDayOfWeek = todayDayOfWeek
)

fun WorkingDayDto.toDomain(): WorkingDay = WorkingDay(
    dayOfWeek = dayOfWeek,
    isClosed = isClosed,
    intervals = intervals.map { it.toDomain() }
)

fun TimeIntervalDto.toDomain(): TimeInterval = TimeInterval(opensAt = opensAt, closesAt = closesAt)

fun CompanyPhotoDto.toDomain(): CompanyPhoto = CompanyPhoto(
    id = id,
    url = url,
    thumbnailUrl = thumbnailUrl,
    width = width,
    height = height,
    sortOrder = sortOrder,
    status = status
)

fun ReviewPhotoDto.toDomain(): ReviewPhoto = ReviewPhoto(
    id = id,
    url = url,
    thumbnailUrl = thumbnailUrl,
    width = width,
    height = height,
    status = status
)

fun CompanyReviewDto.toDomain(): CompanyReview = CompanyReview(
    id = id,
    rating = rating,
    text = text,
    publishedAt = IsoDateParser.parse(publishedAt),
    author = user?.let { CompanyReviewAuthor(it.id, it.name, it.avatarUrl) },
    photos = photos.map { it.toDomain() }
)
