package com.gorod.moygorodok.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class Report(
    val id: Int,
    @SerializedName("reportable_type") val reportableType: ReportableType,
    @SerializedName("reportable_id") val reportableId: Int,
    @SerializedName("reportable_preview") val reportablePreview: String?,
    val reason: ReportReason,
    @SerializedName("reason_label") val reasonLabel: String,
    val comment: String?,
    val status: ReportStatus,
    @SerializedName("status_label") val statusLabel: String,
    @SerializedName("resolution_action") val resolutionAction: ResolutionAction?,
    @SerializedName("resolution_action_label") val resolutionActionLabel: String?,
    @SerializedName("reviewed_at") val reviewedAt: String?,
    @SerializedName("created_at") val createdAt: String,
    val photos: List<ReportPhoto>? = null
)

data class ReportPhoto(
    val id: Int,
    val url: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String?,
    val width: Int?,
    val height: Int?,
    @SerializedName("sort_order") val sortOrder: Int
)

enum class ReportableType(val apiValue: String) {
    @SerializedName("company") COMPANY("company"),
    @SerializedName("company_review") COMPANY_REVIEW("company_review"),
    @SerializedName("user") USER("user"),
    @SerializedName("classified") CLASSIFIED("classified")
}

enum class ReportReason(val apiValue: String, val label: String) {
    @SerializedName("spam") SPAM("spam", "Спам / реклама"),
    @SerializedName("abuse") ABUSE("abuse", "Оскорбления, мат"),
    @SerializedName("false_info") FALSE_INFO("false_info", "Недостоверная информация"),
    @SerializedName("wrong_data") WRONG_DATA("wrong_data", "Неверные данные"),
    @SerializedName("fraud") FRAUD("fraud", "Мошенничество"),
    @SerializedName("inappropriate") INAPPROPRIATE("inappropriate", "Неприемлемый контент"),
    @SerializedName("duplicate") DUPLICATE("duplicate", "Дубликат"),
    @SerializedName("other") OTHER("other", "Другое");

    fun requiresComment(): Boolean = this == OTHER

    companion object {
        fun applicableTo(type: ReportableType): List<ReportReason> = when (type) {
            ReportableType.COMPANY -> listOf(WRONG_DATA, FRAUD, DUPLICATE, INAPPROPRIATE, OTHER)
            ReportableType.COMPANY_REVIEW -> listOf(SPAM, ABUSE, FALSE_INFO, INAPPROPRIATE, OTHER)
            ReportableType.USER -> listOf(SPAM, ABUSE, INAPPROPRIATE, OTHER)
            ReportableType.CLASSIFIED -> listOf(SPAM, ABUSE, FALSE_INFO, FRAUD, INAPPROPRIATE, DUPLICATE, OTHER)
        }
    }
}

enum class ReportStatus {
    @SerializedName("pending") PENDING,
    @SerializedName("accepted") ACCEPTED,
    @SerializedName("rejected") REJECTED
}

enum class ResolutionAction {
    @SerializedName("none") NONE,
    @SerializedName("content_hidden") CONTENT_HIDDEN,
    @SerializedName("content_deleted") CONTENT_DELETED,
    @SerializedName("data_corrected") DATA_CORRECTED,
    @SerializedName("user_warned") USER_WARNED,
    @SerializedName("user_blocked") USER_BLOCKED
}

data class ReportDraft(
    val reportableType: ReportableType,
    val reportableId: Int,
    val reportableTitle: String,
    val reason: ReportReason? = null,
    val comment: String = "",
    val screenshots: List<Uri> = emptyList()
)
