package com.gorod.moygorodok.data.model

data class Delivery(
    val id: String,
    val name: String,
    val description: String,
    val category: DeliveryCategory,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTime: String,
    val deliveryPrice: Double,
    val minOrder: Double,
    val imageColor: String,
    val isOpen: Boolean = true,
    val workingHours: String,
    val address: String,
    val phone: String,
    val menuCategories: List<MenuCategory>,
    val isFavorite: Boolean = false,
    val hasPromo: Boolean = false,
    val promoText: String? = null
)

data class MenuCategory(
    val id: String,
    val name: String,
    val products: List<Product>
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val oldPrice: Double? = null,
    val weight: String? = null,
    val imageColor: String,
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false
)

enum class DeliveryCategory(val displayName: String, val emoji: String) {
    RESTAURANT("Рестораны", "🍽️"),
    FAST_FOOD("Фастфуд", "🍔"),
    PIZZA("Пицца", "🍕"),
    SUSHI("Суши", "🍣"),
    COFFEE("Кофейни", "☕"),
    BAKERY("Выпечка", "🥐"),
    GROCERY("Продукты", "🛒"),
    PHARMACY("Аптеки", "💊")
}

object MockDeliveries {

    fun getAll(): List<Delivery> = listOf(
        Delivery(
            id = "1",
            name = "Пицца Мама Миа",
            description = "Итальянская пиццерия с доставкой",
            category = DeliveryCategory.PIZZA,
            rating = 4.8,
            reviewCount = 256,
            deliveryTime = "30-45 мин",
            deliveryPrice = 0.0,
            minOrder = 500.0,
            imageColor = "#E74C3C",
            workingHours = "10:00 - 23:00",
            address = "ул. Центральная, 25",
            phone = "+7 999 111 22 33",
            hasPromo = true,
            promoText = "Скидка 20% на первый заказ",
            menuCategories = listOf(
                MenuCategory(
                    id = "1-1",
                    name = "Пицца",
                    products = listOf(
                        Product("p1", "Маргарита", "Томатный соус, моцарелла, базилик", 450.0, null, "30 см", "#FF6B6B", isPopular = true),
                        Product("p2", "Пепперони", "Томатный соус, моцарелла, пепперони", 550.0, null, "30 см", "#E74C3C", isPopular = true),
                        Product("p3", "Четыре сыра", "Сливочный соус, моцарелла, пармезан, горгонзола, чеддер", 650.0, null, "30 см", "#F39C12"),
                        Product("p4", "Гавайская", "Томатный соус, моцарелла, курица, ананас", 520.0, null, "30 см", "#27AE60")
                    )
                ),
                MenuCategory(
                    id = "1-2",
                    name = "Напитки",
                    products = listOf(
                        Product("d1", "Кола", "Coca-Cola 0.5л", 90.0, null, "0.5 л", "#2C3E50"),
                        Product("d2", "Сок апельсиновый", "Свежевыжатый", 150.0, null, "0.3 л", "#F39C12")
                    )
                )
            )
        ),
        Delivery(
            id = "2",
            name = "Суши Дракон",
            description = "Японская кухня и роллы",
            category = DeliveryCategory.SUSHI,
            rating = 4.6,
            reviewCount = 189,
            deliveryTime = "40-60 мин",
            deliveryPrice = 150.0,
            minOrder = 800.0,
            imageColor = "#9B59B6",
            workingHours = "11:00 - 22:00",
            address = "пр. Мира, 42",
            phone = "+7 999 222 33 44",
            menuCategories = listOf(
                MenuCategory(
                    id = "2-1",
                    name = "Роллы",
                    products = listOf(
                        Product("r1", "Филадельфия", "Лосось, сыр филадельфия, огурец", 450.0, null, "8 шт", "#E91E63", isPopular = true),
                        Product("r2", "Калифорния", "Краб, авокадо, огурец, икра тобико", 420.0, null, "8 шт", "#FF9800"),
                        Product("r3", "Дракон", "Угорь, авокадо, огурец, соус унаги", 550.0, null, "8 шт", "#9C27B0", isPopular = true)
                    )
                ),
                MenuCategory(
                    id = "2-2",
                    name = "Суши",
                    products = listOf(
                        Product("s1", "Сет Классический", "Нигири лосось, тунец, угорь (6 шт)", 650.0, null, "6 шт", "#3F51B5"),
                        Product("s2", "Гункан с икрой", "Рис, нори, икра лосося", 180.0, null, "2 шт", "#F44336")
                    )
                )
            )
        ),
        Delivery(
            id = "3",
            name = "Бургер Кинг",
            description = "Бургеры, картофель, напитки",
            category = DeliveryCategory.FAST_FOOD,
            rating = 4.3,
            reviewCount = 412,
            deliveryTime = "25-35 мин",
            deliveryPrice = 99.0,
            minOrder = 300.0,
            imageColor = "#FF9800",
            workingHours = "09:00 - 00:00",
            address = "ТЦ «Галерея», 1 этаж",
            phone = "+7 999 333 44 55",
            hasPromo = true,
            promoText = "Комбо за 299₽",
            menuCategories = listOf(
                MenuCategory(
                    id = "3-1",
                    name = "Бургеры",
                    products = listOf(
                        Product("b1", "Чизбургер", "Котлета из говядины, сыр, соус, овощи", 159.0, null, "150 г", "#FF9800", isPopular = true),
                        Product("b2", "Двойной Воппер", "Две котлеты, сыр, бекон, овощи", 349.0, 399.0, "350 г", "#E65100", isPopular = true),
                        Product("b3", "Чикен Роял", "Куриная котлета, салат, майонез", 229.0, null, "200 г", "#FFC107")
                    )
                ),
                MenuCategory(
                    id = "3-2",
                    name = "Картофель",
                    products = listOf(
                        Product("f1", "Картофель фри", "Средняя порция", 99.0, null, "120 г", "#FFD54F"),
                        Product("f2", "Картофель по-деревенски", "С травами", 129.0, null, "150 г", "#FFA726")
                    )
                )
            )
        ),
        Delivery(
            id = "4",
            name = "Кофейня «Зерно»",
            description = "Кофе, десерты, завтраки",
            category = DeliveryCategory.COFFEE,
            rating = 4.9,
            reviewCount = 98,
            deliveryTime = "20-30 мин",
            deliveryPrice = 0.0,
            minOrder = 400.0,
            imageColor = "#795548",
            workingHours = "08:00 - 21:00",
            address = "ул. Книжная, 7",
            phone = "+7 999 444 55 66",
            menuCategories = listOf(
                MenuCategory(
                    id = "4-1",
                    name = "Кофе",
                    products = listOf(
                        Product("c1", "Капучино", "Эспрессо, молоко, пенка", 180.0, null, "300 мл", "#8D6E63", isPopular = true),
                        Product("c2", "Латте", "Эспрессо, молоко", 200.0, null, "400 мл", "#A1887F"),
                        Product("c3", "Раф", "Эспрессо, сливки, ваниль", 250.0, null, "350 мл", "#D7CCC8", isPopular = true)
                    )
                ),
                MenuCategory(
                    id = "4-2",
                    name = "Десерты",
                    products = listOf(
                        Product("ds1", "Чизкейк", "Классический нью-йоркский", 320.0, null, "150 г", "#FFECB3"),
                        Product("ds2", "Тирамису", "С маскарпоне и какао", 350.0, null, "160 г", "#D7CCC8")
                    )
                )
            )
        ),
        Delivery(
            id = "5",
            name = "Пекарня «Хлеб и Ко»",
            description = "Свежая выпечка каждый день",
            category = DeliveryCategory.BAKERY,
            rating = 4.7,
            reviewCount = 156,
            deliveryTime = "30-45 мин",
            deliveryPrice = 100.0,
            minOrder = 300.0,
            imageColor = "#FFA726",
            workingHours = "07:00 - 20:00",
            address = "ул. Пекарная, 3",
            phone = "+7 999 555 66 77",
            menuCategories = listOf(
                MenuCategory(
                    id = "5-1",
                    name = "Хлеб",
                    products = listOf(
                        Product("br1", "Багет французский", "Хрустящий с мягким мякишем", 85.0, null, "250 г", "#FFE0B2"),
                        Product("br2", "Чиабатта", "Итальянский хлеб", 95.0, null, "300 г", "#FFCC80")
                    )
                ),
                MenuCategory(
                    id = "5-2",
                    name = "Выпечка",
                    products = listOf(
                        Product("ps1", "Круассан", "С маслом", 120.0, null, "80 г", "#FFB74D", isPopular = true),
                        Product("ps2", "Пирожок с яблоком", "Слоеное тесто", 75.0, null, "100 г", "#FFA726")
                    )
                )
            )
        ),
        Delivery(
            id = "6",
            name = "Ресторан «Усадьба»",
            description = "Русская и европейская кухня",
            category = DeliveryCategory.RESTAURANT,
            rating = 4.5,
            reviewCount = 78,
            deliveryTime = "45-60 мин",
            deliveryPrice = 200.0,
            minOrder = 1000.0,
            imageColor = "#4CAF50",
            isOpen = false,
            workingHours = "12:00 - 23:00",
            address = "ул. Парковая, 15",
            phone = "+7 999 666 77 88",
            menuCategories = listOf(
                MenuCategory(
                    id = "6-1",
                    name = "Салаты",
                    products = listOf(
                        Product("sl1", "Цезарь с курицей", "Романо, курица, пармезан, соус", 450.0, null, "250 г", "#81C784"),
                        Product("sl2", "Греческий", "Овощи, фета, оливки", 380.0, null, "220 г", "#A5D6A7")
                    )
                ),
                MenuCategory(
                    id = "6-2",
                    name = "Горячее",
                    products = listOf(
                        Product("h1", "Стейк Рибай", "Говядина, овощи гриль", 1200.0, null, "300 г", "#D32F2F", isPopular = true),
                        Product("h2", "Паста Карбонара", "Бекон, пармезан, сливки", 520.0, null, "280 г", "#FFF9C4")
                    )
                )
            )
        )
    )

    fun getById(id: String): Delivery? = getAll().find { it.id == id }

    fun getByCategory(category: DeliveryCategory): List<Delivery> =
        getAll().filter { it.category == category }
}
