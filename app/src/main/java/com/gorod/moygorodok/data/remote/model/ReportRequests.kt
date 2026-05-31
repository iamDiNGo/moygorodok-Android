package com.gorod.moygorodok.data.remote.model

import com.gorod.moygorodok.data.model.Report
import com.gorod.moygorodok.data.model.ReportPhoto
import com.google.gson.annotations.SerializedName

data class CreateReportRequest(
    @SerializedName("reportable_type") val reportableType: String,
    @SerializedName("reportable_id") val reportableId: Int,
    val reason: String,
    val comment: String?
)

data class ReportListResponse(
    val success: Boolean,
    val data: List<Report> = emptyList(),
    val meta: ReportListMeta? = null,
    val message: String? = null
)

data class ReportListMeta(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("per_page") val perPage: Int,
    val total: Int,
    val from: Int?,
    val to: Int?
)

/**
 * Поле `code` из тела ошибки бэка для маппинга 4xx-ответов.
 */
data class ApiErrorBody(
    val success: Boolean? = null,
    val message: String? = null,
    val code: String? = null
)
