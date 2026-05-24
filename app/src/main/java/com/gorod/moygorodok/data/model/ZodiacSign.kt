package com.gorod.moygorodok.data.model

enum class ZodiacSign(
    val slug: String,
    val label: String,
    val symbol: String
) {
    ARIES("aries", "Овен", "♈"),
    TAURUS("taurus", "Телец", "♉"),
    GEMINI("gemini", "Близнецы", "♊"),
    CANCER("cancer", "Рак", "♋"),
    LEO("leo", "Лев", "♌"),
    VIRGO("virgo", "Дева", "♍"),
    LIBRA("libra", "Весы", "♎"),
    SCORPIO("scorpio", "Скорпион", "♏"),
    SAGITTARIUS("sagittarius", "Стрелец", "♐"),
    CAPRICORN("capricorn", "Козерог", "♑"),
    AQUARIUS("aquarius", "Водолей", "♒"),
    PISCES("pisces", "Рыбы", "♓");

    companion object {
        fun fromSlug(slug: String?): ZodiacSign? = values().firstOrNull { it.slug == slug }
    }
}
