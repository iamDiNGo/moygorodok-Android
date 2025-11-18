package com.gorod.moygorodok.data.model

import java.util.Date

data class Ad(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val priceType: PriceType,
    val category: AdCategory,
    val images: List<String>,
    val location: String,
    val seller: Seller,
    val createdAt: Date,
    val viewCount: Int = 0,
    val isFavorite: Boolean = false,
    val isActive: Boolean = true
)

data class Seller(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Float,
    val adsCount: Int
)

enum class PriceType(val displayName: String) {
    FIXED("Фиксированная"),
    NEGOTIABLE("Договорная"),
    FREE("Бесплатно"),
    EXCHANGE("Обмен")
}

enum class AdCategory(val displayName: String) {
    TRANSPORT("Транспорт"),
    REALTY("Недвижимость"),
    ELECTRONICS("Электроника"),
    CLOTHING("Одежда"),
    HOME("Дом и сад"),
    SERVICES("Услуги"),
    ANIMALS("Животные"),
    HOBBIES("Хобби и отдых"),
    CHILDREN("Детские товары"),
    OTHER("Другое")
}

enum class SortOption(val displayName: String) {
    DATE_DESC("Сначала новые"),
    DATE_ASC("Сначала старые"),
    PRICE_ASC("Сначала дешевые"),
    PRICE_DESC("Сначала дорогие"),
    POPULAR("По популярности")
}

data class AdFilter(
    val category: AdCategory? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val searchQuery: String? = null,
    val sortOption: SortOption = SortOption.DATE_DESC
)

// Фиктивные данные для тестирования
object MockAds {
    private val sellers = listOf(
        Seller("s1", "Иван Петров", "+7 999 111 22 33", 4.8f, 15),
        Seller("s2", "Мария Сидорова", "+7 999 222 33 44", 4.5f, 8),
        Seller("s3", "Алексей Козлов", "+7 999 333 44 55", 4.9f, 23),
        Seller("s4", "Елена Новикова", "+7 999 444 55 66", 4.2f, 5),
        Seller("s5", "Дмитрий Волков", "+7 999 555 66 77", 4.7f, 12)
    )

    private val ads = listOf(
        Ad(
            id = "ad1",
            title = "iPhone 14 Pro Max 256GB",
            description = """
                Продаю iPhone 14 Pro Max 256GB в идеальном состоянии.

                Характеристики:
                - Цвет: Deep Purple
                - Память: 256GB
                - Состояние батареи: 98%
                - Комплект: коробка, документы, зарядка

                Телефон всегда был в чехле и с защитным стеклом.
                Никаких царапин и сколов. Face ID работает отлично.

                Причина продажи: переход на Android.

                Торг уместен при быстрой сделке.
            """.trimIndent(),
            price = 85000.0,
            priceType = PriceType.NEGOTIABLE,
            category = AdCategory.ELECTRONICS,
            images = listOf(),
            location = "Центральный район",
            seller = sellers[0],
            createdAt = Date(System.currentTimeMillis() - 3600000),
            viewCount = 234
        ),
        Ad(
            id = "ad2",
            title = "2-комнатная квартира, 54 м²",
            description = """
                Сдается 2-комнатная квартира в новом доме.

                Характеристики:
                - Площадь: 54 м²
                - Этаж: 7/16
                - Ремонт: евроремонт
                - Мебель: полностью меблирована
                - Техника: холодильник, стиральная машина, ТВ

                Рядом:
                - Остановка общественного транспорта - 3 мин
                - Супермаркет - 5 мин
                - Школа и детский сад - 10 мин

                Условия:
                - Оплата помесячная
                - Залог: 1 месяц
                - Коммунальные услуги включены

                Без животных и курящих.
            """.trimIndent(),
            price = 35000.0,
            priceType = PriceType.FIXED,
            category = AdCategory.REALTY,
            images = listOf(),
            location = "Заречный район",
            seller = sellers[1],
            createdAt = Date(System.currentTimeMillis() - 7200000),
            viewCount = 567
        ),
        Ad(
            id = "ad3",
            title = "Велосипед горный Scott Scale 970",
            description = """
                Продаю горный велосипед Scott Scale 970.

                Характеристики:
                - Рама: алюминий, размер L
                - Колеса: 29"
                - Вилка: воздушная RockShox
                - Переключатели: Shimano Deore
                - Тормоза: гидравлика Shimano

                Велосипед в отличном состоянии, пробег около 500 км.
                Регулярное ТО, все работает идеально.

                В подарок: насос, замок, крылья.
            """.trimIndent(),
            price = 45000.0,
            priceType = PriceType.NEGOTIABLE,
            category = AdCategory.TRANSPORT,
            images = listOf(),
            location = "Советский район",
            seller = sellers[2],
            createdAt = Date(System.currentTimeMillis() - 14400000),
            viewCount = 189
        ),
        Ad(
            id = "ad4",
            title = "Котята британские, 2 месяца",
            description = """
                Продаются котята британской породы.

                - Возраст: 2 месяца
                - Окрас: голубой, лиловый
                - Пол: 2 мальчика, 1 девочка

                Котята:
                - Приучены к лотку
                - Едят сухой и влажный корм
                - Игривые и ласковые
                - С документами (родословная)

                Родители чемпионы, можно посмотреть.

                К переезду в новый дом готовы!
            """.trimIndent(),
            price = 15000.0,
            priceType = PriceType.FIXED,
            category = AdCategory.ANIMALS,
            images = listOf(),
            location = "Ленинский район",
            seller = sellers[3],
            createdAt = Date(System.currentTimeMillis() - 28800000),
            viewCount = 423
        ),
        Ad(
            id = "ad5",
            title = "Диван угловой с оттоманкой",
            description = """
                Продается угловой диван в отличном состоянии.

                Характеристики:
                - Размер: 280x180 см
                - Спальное место: 200x150 см
                - Механизм: дельфин
                - Обивка: велюр, серый цвет
                - Наполнитель: ППУ

                Есть вместительный ящик для белья.

                Диван покупался 2 года назад, использовался в гостевой комнате.
                Состояние идеальное, без пятен и потертостей.

                Самовывоз. Помогу с погрузкой.
            """.trimIndent(),
            price = 28000.0,
            priceType = PriceType.NEGOTIABLE,
            category = AdCategory.HOME,
            images = listOf(),
            location = "Октябрьский район",
            seller = sellers[4],
            createdAt = Date(System.currentTimeMillis() - 43200000),
            viewCount = 156
        ),
        Ad(
            id = "ad6",
            title = "Репетитор по математике",
            description = """
                Опытный репетитор по математике.

                Предлагаю:
                - Подготовка к ЕГЭ и ОГЭ
                - Помощь школьникам 5-11 классов
                - Устранение пробелов в знаниях
                - Подготовка к олимпиадам

                Обо мне:
                - Образование: МГУ, мехмат
                - Стаж преподавания: 8 лет
                - Средний балл учеников на ЕГЭ: 82

                Занятия проводятся:
                - У меня (Центральный район)
                - Онлайн (Zoom, Skype)

                Первое занятие - бесплатная диагностика!
            """.trimIndent(),
            price = 1500.0,
            priceType = PriceType.FIXED,
            category = AdCategory.SERVICES,
            images = listOf(),
            location = "Центральный район",
            seller = sellers[0],
            createdAt = Date(System.currentTimeMillis() - 86400000),
            viewCount = 312
        ),
        Ad(
            id = "ad7",
            title = "Детская коляска 3 в 1 Adamex",
            description = """
                Продается коляска Adamex Reggio 3 в 1.

                В комплекте:
                - Люлька
                - Прогулочный блок
                - Автокресло 0+
                - Сумка для мамы
                - Дождевик и москитная сетка

                Особенности:
                - Надувные колеса
                - Регулируемая ручка
                - Большая корзина

                Состояние отличное, использовалась 1 год.
                Цвет: серый с мятным.
            """.trimIndent(),
            price = 18000.0,
            priceType = PriceType.NEGOTIABLE,
            category = AdCategory.CHILDREN,
            images = listOf(),
            location = "Заречный район",
            seller = sellers[1],
            createdAt = Date(System.currentTimeMillis() - 172800000),
            viewCount = 245
        ),
        Ad(
            id = "ad8",
            title = "Куртка зимняя мужская, размер L",
            description = """
                Продаю зимнюю куртку в отличном состоянии.

                Характеристики:
                - Бренд: Columbia
                - Размер: L (50-52)
                - Наполнитель: пух/перо
                - Цвет: темно-синий

                Температурный режим до -30°C.
                Капюшон отстегивается.
                Множество карманов.

                Носилась один сезон, состояние как новая.
            """.trimIndent(),
            price = 8000.0,
            priceType = PriceType.FIXED,
            category = AdCategory.CLOTHING,
            images = listOf(),
            location = "Советский район",
            seller = sellers[2],
            createdAt = Date(System.currentTimeMillis() - 259200000),
            viewCount = 98
        ),
        Ad(
            id = "ad9",
            title = "Гитара акустическая Yamaha F310",
            description = """
                Продается акустическая гитара Yamaha F310.

                Характеристики:
                - Тип: дредноут
                - Верхняя дека: ель
                - Нижняя дека и обечайки: меранти
                - Гриф: нато
                - Накладка: палисандр

                Гитара в идеальном состоянии, звук отличный.
                Идеально подходит для начинающих и среднего уровня.

                В подарок: чехол, медиаторы, струны.
            """.trimIndent(),
            price = 12000.0,
            priceType = PriceType.NEGOTIABLE,
            category = AdCategory.HOBBIES,
            images = listOf(),
            location = "Ленинский район",
            seller = sellers[3],
            createdAt = Date(System.currentTimeMillis() - 345600000),
            viewCount = 167
        ),
        Ad(
            id = "ad10",
            title = "Отдам даром книги",
            description = """
                Отдам бесплатно книги в хорошем состоянии.

                Что есть:
                - Классика русской литературы
                - Детективы
                - Фантастика
                - Учебники по программированию
                - Детские книги

                Всего около 50 книг.

                Можно забрать все или выбрать что нужно.
                Самовывоз из Октябрьского района.

                Пишите, скину фото!
            """.trimIndent(),
            price = 0.0,
            priceType = PriceType.FREE,
            category = AdCategory.OTHER,
            images = listOf(),
            location = "Октябрьский район",
            seller = sellers[4],
            createdAt = Date(System.currentTimeMillis() - 432000000),
            viewCount = 534
        )
    )

    fun getAll(): List<Ad> = ads

    fun getById(id: String): Ad? = ads.find { it.id == id }

    fun getFiltered(filter: AdFilter): List<Ad> {
        var result = ads.toList()

        // Фильтр по категории
        filter.category?.let { category ->
            result = result.filter { it.category == category }
        }

        // Фильтр по цене
        filter.minPrice?.let { min ->
            result = result.filter { it.price >= min }
        }
        filter.maxPrice?.let { max ->
            result = result.filter { it.price <= max }
        }

        // Поиск по тексту
        filter.searchQuery?.let { query ->
            if (query.isNotBlank()) {
                val lowerQuery = query.lowercase()
                result = result.filter {
                    it.title.lowercase().contains(lowerQuery) ||
                    it.description.lowercase().contains(lowerQuery)
                }
            }
        }

        // Сортировка
        result = when (filter.sortOption) {
            SortOption.DATE_DESC -> result.sortedByDescending { it.createdAt }
            SortOption.DATE_ASC -> result.sortedBy { it.createdAt }
            SortOption.PRICE_ASC -> result.sortedBy { it.price }
            SortOption.PRICE_DESC -> result.sortedByDescending { it.price }
            SortOption.POPULAR -> result.sortedByDescending { it.viewCount }
        }

        return result
    }
}
