package com.gorod.moygorodok.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Парсит ISO 8601 в Date. Поддерживает форматы бэка:
 * - `2026-05-24T10:15:00Z` (без миллисекунд)
 * - `2026-05-24T10:15:00.000000Z` (Laravel — 6 цифр микросекунд)
 * - `2026-05-24T10:15:00+03:00` (с таймзоной)
 *
 * Дробная часть нормализуется до миллисекунд перед парсингом
 * (SimpleDateFormat некорректно работает с 6 цифрами SSSSSS).
 */
object IsoDateParser {

    private val parsers = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ROOT),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
    )

    private val fractionalRegex = Regex("""\.(\d{1,9})""")

    fun parse(raw: String?): Date? {
        if (raw.isNullOrBlank()) return null
        val normalized = normalizeFraction(raw)
        for (parser in parsers) {
            try {
                return parser.parse(normalized) ?: continue
            } catch (_: ParseException) {
                continue
            }
        }
        return null
    }

    /** `…:00.000000Z` → `…:00.000Z`. Если дробной части нет — возвращает строку без изменений. */
    private fun normalizeFraction(raw: String): String =
        fractionalRegex.replace(raw) { match ->
            val digits = match.groupValues[1]
            val trimmed = if (digits.length >= 3) digits.substring(0, 3) else digits.padEnd(3, '0')
            ".$trimmed"
        }
}
