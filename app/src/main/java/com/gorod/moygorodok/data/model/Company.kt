package com.gorod.moygorodok.data.model

data class Company(
    val id: String,
    val name: String,
    val category: CompanyCategory,
    val logoColor: String,
    val description: String,
    val shortDescription: String,
    val address: String,
    val phone: String,
    val email: String,
    val website: String,
    val workingHours: String,
    val rating: Float,
    val reviewsCount: Int,
    val isVerified: Boolean,
    val services: List<String>,
    val photos: List<String> // Colors for placeholders
)

enum class CompanyCategory(
    val displayName: String,
    val emoji: String,
    val color: String
) {
    RETAIL("Магазины", "🛒", "#4CAF50"),
    FOOD("Рестораны и кафе", "🍽️", "#FF9800"),
    SERVICES("Услуги", "🔧", "#2196F3"),
    HEALTH("Здоровье", "🏥", "#F44336"),
    BEAUTY("Красота", "💇", "#E91E63"),
    EDUCATION("Образование", "📚", "#9C27B0"),
    AUTO("Авто", "🚗", "#607D8B"),
    FINANCE("Финансы", "🏦", "#795548"),
    REALTY("Недвижимость", "🏠", "#00BCD4"),
    ENTERTAINMENT("Развлечения", "🎮", "#FF5722")
}

object MockCompanies {

    private val companies = listOf(
        Company(
            id = "1",
            name = "СуперМаркет 'Продукты'",
            category = CompanyCategory.RETAIL,
            logoColor = "#4CAF50",
            description = "Крупнейшая сеть продуктовых магазинов в городе. Более 10 000 наименований товаров по доступным ценам. Свежие продукты каждый день, собственное производство выпечки и готовых блюд. Регулярные акции и скидки для постоянных покупателей.",
            shortDescription = "Продукты по доступным ценам",
            address = "ул. Центральная, 15",
            phone = "+7 (999) 123-45-67",
            email = "info@supermarket.ru",
            website = "www.supermarket.ru",
            workingHours = "08:00 - 22:00",
            rating = 4.5f,
            reviewsCount = 234,
            isVerified = true,
            services = listOf("Доставка", "Самовывоз", "Карта лояльности"),
            photos = listOf("#81C784", "#66BB6A", "#4CAF50")
        ),
        Company(
            id = "2",
            name = "Ресторан 'Итальяно'",
            category = CompanyCategory.FOOD,
            logoColor = "#FF9800",
            description = "Аутентичная итальянская кухня в самом сердце города. Пицца из дровяной печи, домашняя паста, изысканные десерты. Уютная атмосфера и профессиональное обслуживание. Идеальное место для романтического ужина или семейного обеда.",
            shortDescription = "Итальянская кухня",
            address = "пр. Мира, 42",
            phone = "+7 (999) 234-56-78",
            email = "booking@italiano.ru",
            website = "www.italiano.ru",
            workingHours = "11:00 - 23:00",
            rating = 4.8f,
            reviewsCount = 456,
            isVerified = true,
            services = listOf("Бронирование", "Доставка", "Банкеты", "Wi-Fi"),
            photos = listOf("#FFB74D", "#FFA726", "#FF9800")
        ),
        Company(
            id = "3",
            name = "Автосервис 'Мастер'",
            category = CompanyCategory.AUTO,
            logoColor = "#607D8B",
            description = "Профессиональный автосервис полного цикла. Диагностика, ремонт двигателей, ходовой части, электрики. Кузовной ремонт и покраска. Шиномонтаж и балансировка. Оригинальные запчасти и гарантия на все виды работ.",
            shortDescription = "Ремонт и обслуживание авто",
            address = "ул. Промышленная, 8",
            phone = "+7 (999) 345-67-89",
            email = "service@master-auto.ru",
            website = "www.master-auto.ru",
            workingHours = "09:00 - 20:00",
            rating = 4.6f,
            reviewsCount = 189,
            isVerified = true,
            services = listOf("Диагностика", "Ремонт", "Шиномонтаж", "Мойка"),
            photos = listOf("#90A4AE", "#78909C", "#607D8B")
        ),
        Company(
            id = "4",
            name = "Стоматология 'Улыбка'",
            category = CompanyCategory.HEALTH,
            logoColor = "#F44336",
            description = "Современная стоматологическая клиника с полным спектром услуг. Терапия, хирургия, ортодонтия, имплантация. Новейшее оборудование и опытные специалисты. Безболезненное лечение и индивидуальный подход к каждому пациенту.",
            shortDescription = "Стоматологические услуги",
            address = "ул. Здоровья, 23",
            phone = "+7 (999) 456-78-90",
            email = "smile@dental.ru",
            website = "www.smile-dental.ru",
            workingHours = "09:00 - 21:00",
            rating = 4.9f,
            reviewsCount = 567,
            isVerified = true,
            services = listOf("Терапия", "Хирургия", "Ортодонтия", "Имплантация"),
            photos = listOf("#EF5350", "#E53935", "#F44336")
        ),
        Company(
            id = "5",
            name = "Салон красоты 'Стиль'",
            category = CompanyCategory.BEAUTY,
            logoColor = "#E91E63",
            description = "Премиальный салон красоты для тех, кто ценит качество. Стрижки и укладки, окрашивание, маникюр и педикюр, косметология. Профессиональная косметика и индивидуальный подход. Создаем красоту с любовью.",
            shortDescription = "Парикмахерские услуги и маникюр",
            address = "ул. Красоты, 7",
            phone = "+7 (999) 567-89-01",
            email = "style@beauty.ru",
            website = "www.style-beauty.ru",
            workingHours = "10:00 - 20:00",
            rating = 4.7f,
            reviewsCount = 312,
            isVerified = true,
            services = listOf("Стрижки", "Окрашивание", "Маникюр", "Косметология"),
            photos = listOf("#F06292", "#EC407A", "#E91E63")
        ),
        Company(
            id = "6",
            name = "Образовательный центр 'Знание'",
            category = CompanyCategory.EDUCATION,
            logoColor = "#9C27B0",
            description = "Образовательный центр для детей и взрослых. Подготовка к школе, репетиторство, языковые курсы, программирование. Опытные преподаватели и современные методики обучения. Гарантируем результат.",
            shortDescription = "Курсы и репетиторство",
            address = "ул. Ученая, 12",
            phone = "+7 (999) 678-90-12",
            email = "info@znanie.ru",
            website = "www.znanie-center.ru",
            workingHours = "09:00 - 21:00",
            rating = 4.8f,
            reviewsCount = 278,
            isVerified = true,
            services = listOf("Языки", "Программирование", "Репетиторство", "Подготовка к ЕГЭ"),
            photos = listOf("#BA68C8", "#AB47BC", "#9C27B0")
        ),
        Company(
            id = "7",
            name = "Банк 'Городской'",
            category = CompanyCategory.FINANCE,
            logoColor = "#795548",
            description = "Надежный банк с многолетней историей. Кредиты, вклады, ипотека, инвестиции. Выгодные условия для физических и юридических лиц. Онлайн-банкинг и мобильное приложение. Ваши финансы под надежной защитой.",
            shortDescription = "Банковские услуги",
            address = "пр. Финансовый, 1",
            phone = "+7 (999) 789-01-23",
            email = "info@gorodbank.ru",
            website = "www.gorodbank.ru",
            workingHours = "09:00 - 18:00",
            rating = 4.3f,
            reviewsCount = 145,
            isVerified = true,
            services = listOf("Кредиты", "Вклады", "Ипотека", "Переводы"),
            photos = listOf("#A1887F", "#8D6E63", "#795548")
        ),
        Company(
            id = "8",
            name = "Агентство недвижимости 'Дом'",
            category = CompanyCategory.REALTY,
            logoColor = "#00BCD4",
            description = "Профессиональное агентство недвижимости. Купля-продажа квартир и домов, аренда, коммерческая недвижимость. Юридическое сопровождение сделок. Бесплатная оценка вашей недвижимости.",
            shortDescription = "Покупка, продажа, аренда",
            address = "ул. Домашняя, 30",
            phone = "+7 (999) 890-12-34",
            email = "dom@realty.ru",
            website = "www.dom-realty.ru",
            workingHours = "09:00 - 19:00",
            rating = 4.4f,
            reviewsCount = 198,
            isVerified = true,
            services = listOf("Покупка", "Продажа", "Аренда", "Оценка"),
            photos = listOf("#4DD0E1", "#26C6DA", "#00BCD4")
        ),
        Company(
            id = "9",
            name = "Развлекательный центр 'Веселье'",
            category = CompanyCategory.ENTERTAINMENT,
            logoColor = "#FF5722",
            description = "Крупнейший развлекательный центр города. Боулинг, бильярд, игровые автоматы, детская комната. Кафе и бар. Проведение праздников и корпоративов. Веселье для всей семьи!",
            shortDescription = "Боулинг, бильярд, игры",
            address = "ТРЦ Галактика, 4 этаж",
            phone = "+7 (999) 901-23-45",
            email = "fun@veselie.ru",
            website = "www.veselie-center.ru",
            workingHours = "10:00 - 02:00",
            rating = 4.5f,
            reviewsCount = 423,
            isVerified = true,
            services = listOf("Боулинг", "Бильярд", "Праздники", "Детская комната"),
            photos = listOf("#FF8A65", "#FF7043", "#FF5722")
        ),
        Company(
            id = "10",
            name = "Ремонт техники 'Мастерская'",
            category = CompanyCategory.SERVICES,
            logoColor = "#2196F3",
            description = "Ремонт любой бытовой техники и электроники. Телефоны, ноутбуки, телевизоры, стиральные машины, холодильники. Бесплатная диагностика, гарантия на работы. Выезд мастера на дом.",
            shortDescription = "Ремонт техники и электроники",
            address = "ул. Техническая, 5",
            phone = "+7 (999) 012-34-56",
            email = "repair@mastersk.ru",
            website = "www.mastersk-repair.ru",
            workingHours = "09:00 - 19:00",
            rating = 4.6f,
            reviewsCount = 267,
            isVerified = false,
            services = listOf("Телефоны", "Ноутбуки", "Бытовая техника", "Выезд на дом"),
            photos = listOf("#64B5F6", "#42A5F5", "#2196F3")
        )
    )

    fun getCompanies(): List<Company> = companies

    fun getCompanyById(id: String): Company? = companies.find { it.id == id }

    fun getCompaniesByCategory(category: CompanyCategory): List<Company> =
        companies.filter { it.category == category }

    fun getCategories(): List<CompanyCategory> = CompanyCategory.values().toList()

    fun getTotalCount(): Int = companies.size

    fun getVerifiedCount(): Int = companies.count { it.isVerified }

    fun getTopRated(): List<Company> = companies.sortedByDescending { it.rating }.take(3)
}
