package com.gorod.moygorodok.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Парсит ISO 8601 (`2026-05-24T10:15:00Z`) и форматирует в локаль `ru-RU`.
 */
object ReportDateFormatter {

    private val isoParsers = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ROOT)
    )

    private val shortFormatter = SimpleDateFormat("d MMMM yyyy", Locale("ru", "RU"))
    private val longFormatter = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru", "RU"))

    fun formatShort(iso: String?): String = format(iso, shortFormatter)

    fun formatLong(iso: String?): String = format(iso, longFormatter)

    private fun format(iso: String?, target: SimpleDateFormat): String {
        if (iso.isNullOrBlank()) return ""
        for (parser in isoParsers) {
            try {
                val date = parser.parse(iso) ?: continue
                return target.format(date)
            } catch (_: ParseException) {
                continue
            }
        }
        return iso
    }
}
