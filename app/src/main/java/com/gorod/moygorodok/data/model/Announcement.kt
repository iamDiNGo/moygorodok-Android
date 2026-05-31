package com.gorod.moygorodok.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Доменная модель объявления — зеркало серверного контракта (см. MOBILE_API §6).
 * Часть полей опциональна, потому что одна и та же модель ходит и в List, и в Detail, и в My.
 */
data class Announcement(
    val id: Int,
    @SerializedName("city_id") val cityId: Int?,
    val category: AnnouncementCategory,
    @SerializedName("category_label") val categoryLabel: String,
    @SerializedName("category_icon") val categoryIcon: String?,
    val title: String,
    val description: String? = null,
    val price: Double? = null,
    @SerializedName("price_formatted") val priceFormatted: String,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: AnnouncementStatus? = null,
    @SerializedName("status_label") val statusLabel: String? = null,
    @SerializedName("expires_at") val expiresAt: String? = null,
    @SerializedName("views_count") val viewsCount: Int = 0,
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
    @SerializedName("is_owner") val isOwner: Boolean? = null,
    val author: Author? = null,
    val photos: List<AnnouncementPhoto>? = null,
    @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("days_until_expire") val daysUntilExpire: Int? = null
) {

    val createdDate: Date?
        get() = createdAt?.let { parseIso(it) }

    /** «5 мин. назад», «3 ч. назад», «2 дн. назад», «Только что». */
    fun timeAgo(now: Date = Date()): String {
        val date = createdDate ?: return ""
        val diff = now.time - date.time
        val minutes = diff / 60_000
        val hours = minutes / 60
        val days = hours / 24
        return when {
            days > 0 -> "$days дн. назад"
            hours > 0 -> "$hours ч. назад"
            minutes > 0 -> "$minutes мин. назад"
            else -> "Только что"
        }
    }

    companion object {
        private val isoParsers = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            },
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
        )

        fun parseIso(raw: String): Date? {
            for (parser in isoParsers) {
                runCatching { return parser.parse(raw) }
            }
            return null
        }
    }
}

data class Author(
    val id: Int,
    val name: String,
    val phone: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

data class AnnouncementPhoto(
    val id: Int,
    val url: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    @SerializedName("sort_order") val sortOrder: Int = 0
)

enum class AnnouncementCategory(val apiValue: String, val label: String) {
    @SerializedName("real_estate") REAL_ESTATE("real_estate", "Недвижимость"),
    @SerializedName("transport") TRANSPORT("transport", "Транспорт"),
    @SerializedName("electronics") ELECTRONICS("electronics", "Электроника"),
    @SerializedName("clothes") CLOTHES("clothes", "Одежда"),
    @SerializedName("furniture") FURNITURE("furniture", "Мебель"),
    @SerializedName("services") SERVICES("services", "Услуги"),
    @SerializedName("pets") PETS("pets", "Животные"),
    @SerializedName("other") OTHER("other", "Другое");

    companion object {
        fun fromApi(value: String?): AnnouncementCategory? =
            entries.firstOrNull { it.apiValue == value }
    }
}

enum class AnnouncementStatus(val apiValue: String, val label: String) {
    @SerializedName("published") PUBLISHED("published", "Опубликовано"),
    @SerializedName("expired") EXPIRED("expired", "Истекло"),
    @SerializedName("closed") CLOSED("closed", "Закрыто");

    companion object {
        fun fromApi(value: String?): AnnouncementStatus? =
            entries.firstOrNull { it.apiValue == value }
    }
}

enum class AnnouncementSortOption(val apiValue: String, val label: String) {
    DATE_DESC("date_desc", "Сначала новые"),
    DATE_ASC("date_asc", "Сначала старые"),
    PRICE_ASC("price_asc", "Сначала дешёвые"),
    PRICE_DESC("price_desc", "Сначала дорогие")
}

/**
 * Снимок активных фильтров на экране списка.
 * `category == null` означает «все категории».
 */
data class AnnouncementFilter(
    val category: AnnouncementCategory? = null,
    val search: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val sort: AnnouncementSortOption = AnnouncementSortOption.DATE_DESC
) {
    val hasActiveFilters: Boolean
        get() = category != null || !search.isNullOrBlank() || minPrice != null || maxPrice != null
}
