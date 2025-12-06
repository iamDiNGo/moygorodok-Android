package com.gorod.moygorodok.data.model

data class DeliveryAdmin(
    val id: String,
    val delivery: Delivery,
    val isOpen: Boolean = true,
    val deliveryDiscount: Int = 0, // percentage
    val freeDeliveryFrom: Double? = null,
    val deliveryZone: DeliveryZone? = null,
    val promotions: List<Promotion> = emptyList()
)

data class DeliveryZone(
    val centerLat: Double,
    val centerLng: Double,
    val radiusKm: Double,
    val polygonPoints: List<GeoPoint>? = null
)

data class GeoPoint(
    val lat: Double,
    val lng: Double
)

data class Promotion(
    val id: String,
    val title: String,
    val description: String,
    val discountPercent: Int? = null,
    val discountAmount: Double? = null,
    val minOrderAmount: Double? = null,
    val promoCode: String? = null,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean = true,
    val imageColor: String = "#FF6B6B"
)

data class EditableCategory(
    val id: String,
    val name: String,
    val sortOrder: Int,
    val isVisible: Boolean = true
)

data class EditableProduct(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val price: Double,
    val oldPrice: Double? = null,
    val weight: String? = null,
    val imageColor: String,
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val sortOrder: Int
)

object MockDeliveryAdmin {

    private val deliveryAdmin = DeliveryAdmin(
        id = "admin1",
        delivery = MockDeliveries.getAll().first(),
        isOpen = true,
        deliveryDiscount = 10,
        freeDeliveryFrom = 1500.0,
        deliveryZone = DeliveryZone(
            centerLat = 55.7558,
            centerLng = 37.6173,
            radiusKm = 5.0,
            polygonPoints = null
        ),
        promotions = listOf(
            Promotion(
                id = "promo1",
                title = "Скидка 20% на первый заказ",
                description = "Получите скидку 20% на ваш первый заказ в нашем заведении",
                discountPercent = 20,
                minOrderAmount = 500.0,
                promoCode = "FIRST20",
                startDate = "01.11.2024",
                endDate = "31.12.2024",
                isActive = true,
                imageColor = "#FF6B6B"
            ),
            Promotion(
                id = "promo2",
                title = "Бесплатная доставка",
                description = "Бесплатная доставка при заказе от 1000 рублей",
                discountAmount = 200.0,
                minOrderAmount = 1000.0,
                startDate = "15.11.2024",
                endDate = "15.12.2024",
                isActive = true,
                imageColor = "#4ECDC4"
            ),
            Promotion(
                id = "promo3",
                title = "Счастливые часы",
                description = "Скидка 15% на все меню с 14:00 до 17:00",
                discountPercent = 15,
                startDate = "01.11.2024",
                endDate = "30.11.2024",
                isActive = false,
                imageColor = "#FFEAA7"
            )
        )
    )

    fun getDeliveryAdmin(): DeliveryAdmin = deliveryAdmin

    fun getEditableCategories(): List<EditableCategory> {
        val delivery = deliveryAdmin.delivery
        return delivery.menuCategories.mapIndexed { index, category ->
            EditableCategory(
                id = category.id,
                name = category.name,
                sortOrder = index,
                isVisible = true
            )
        }
    }

    fun getEditableProducts(): List<EditableProduct> {
        val delivery = deliveryAdmin.delivery
        return delivery.menuCategories.flatMap { category ->
            category.products.mapIndexed { index, product ->
                EditableProduct(
                    id = product.id,
                    categoryId = category.id,
                    name = product.name,
                    description = product.description,
                    price = product.price,
                    oldPrice = product.oldPrice,
                    weight = product.weight,
                    imageColor = product.imageColor,
                    isAvailable = product.isAvailable,
                    isPopular = product.isPopular,
                    sortOrder = index
                )
            }
        }
    }

    fun getPromotions(): List<Promotion> = deliveryAdmin.promotions
}
