package com.gorod.moygorodok.ui.company.common

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import android.graphics.Color
import com.gorod.moygorodok.R

/**
 * Локальный mapper категории заведения → drawable. Бэк отдаёт SF Symbol-имена
 * (`icon_key`) для iOS, на Android держим свой словарь Material иконок.
 */
object CompanyCategoryIcons {

    private const val DEFAULT_COLOR = "#90A4AE"

    @DrawableRes
    fun resolveIcon(categoryKey: String?): Int = when (categoryKey) {
        "restaurant"    -> R.drawable.ic_restaurant
        "cafe"          -> R.drawable.ic_cafe
        "shop"          -> R.drawable.ic_shop
        "beauty"        -> R.drawable.ic_beauty
        "health"        -> R.drawable.ic_health
        "auto"          -> R.drawable.ic_car
        "education"     -> R.drawable.ic_education
        "sport"         -> R.drawable.ic_sport
        "entertainment" -> R.drawable.ic_entertainment
        "services"      -> R.drawable.ic_services
        "construction"  -> R.drawable.ic_construction
        "finance"       -> R.drawable.ic_finance
        else            -> R.drawable.ic_company_default
    }

    @ColorInt
    fun resolveColor(colorHex: String?): Int = runCatching {
        Color.parseColor(colorHex ?: DEFAULT_COLOR)
    }.getOrElse { Color.parseColor(DEFAULT_COLOR) }
}
