package com.gorod.moygorodok.ui.announcements

import androidx.annotation.DrawableRes
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.AnnouncementCategory

/**
 * Локальный mapper категории → drawable. Бэк отдаёт SF Symbol-имена (iOS),
 * клиент держит свой словарь иконок.
 */
object AnnouncementCategoryIcons {

    @DrawableRes
    fun iconRes(category: AnnouncementCategory): Int = when (category) {
        AnnouncementCategory.REAL_ESTATE -> R.drawable.ic_category_real_estate
        AnnouncementCategory.TRANSPORT -> R.drawable.ic_category_transport
        AnnouncementCategory.ELECTRONICS -> R.drawable.ic_category_electronics
        AnnouncementCategory.CLOTHES -> R.drawable.ic_category_clothes
        AnnouncementCategory.FURNITURE -> R.drawable.ic_category_furniture
        AnnouncementCategory.SERVICES -> R.drawable.ic_category_services
        AnnouncementCategory.PETS -> R.drawable.ic_category_pets
        AnnouncementCategory.OTHER -> R.drawable.ic_category_other
    }
}
