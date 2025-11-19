package com.gorod.moygorodok.data.model

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String,
    val buyRate: Double,
    val sellRate: Double,
    val cbRate: Double, // Central Bank rate
    val change: Double, // Change from previous day
    val color: String
)

data class CurrencyHistory(
    val date: String,
    val rate: Double
)

object MockCurrencies {

    private val currencies = listOf(
        Currency(
            code = "USD",
            name = "Доллар США",
            symbol = "$",
            flag = "🇺🇸",
            buyRate = 91.50,
            sellRate = 93.20,
            cbRate = 92.35,
            change = 0.45,
            color = "#4CAF50"
        ),
        Currency(
            code = "EUR",
            name = "Евро",
            symbol = "€",
            flag = "🇪🇺",
            buyRate = 99.80,
            sellRate = 101.50,
            cbRate = 100.65,
            change = -0.32,
            color = "#2196F3"
        ),
        Currency(
            code = "CNY",
            name = "Китайский юань",
            symbol = "¥",
            flag = "🇨🇳",
            buyRate = 12.60,
            sellRate = 13.10,
            cbRate = 12.85,
            change = 0.08,
            color = "#F44336"
        ),
        Currency(
            code = "JPY",
            name = "Японская йена",
            symbol = "¥",
            flag = "🇯🇵",
            buyRate = 0.61,
            sellRate = 0.64,
            cbRate = 0.625,
            change = -0.005,
            color = "#E91E63"
        ),
        Currency(
            code = "GBP",
            name = "Фунт стерлингов",
            symbol = "£",
            flag = "🇬🇧",
            buyRate = 116.20,
            sellRate = 118.80,
            cbRate = 117.50,
            change = 0.85,
            color = "#9C27B0"
        ),
        Currency(
            code = "CHF",
            name = "Швейцарский франк",
            symbol = "₣",
            flag = "🇨🇭",
            buyRate = 103.40,
            sellRate = 105.60,
            cbRate = 104.50,
            change = 0.22,
            color = "#FF5722"
        ),
        Currency(
            code = "TRY",
            name = "Турецкая лира",
            symbol = "₺",
            flag = "🇹🇷",
            buyRate = 2.68,
            sellRate = 2.85,
            cbRate = 2.76,
            change = -0.03,
            color = "#795548"
        ),
        Currency(
            code = "KZT",
            name = "Казахстанский тенге",
            symbol = "₸",
            flag = "🇰🇿",
            buyRate = 0.19,
            sellRate = 0.21,
            cbRate = 0.20,
            change = 0.002,
            color = "#00BCD4"
        )
    )

    fun getCurrencies(): List<Currency> = currencies

    fun getMainCurrencies(): List<Currency> = currencies.filter {
        it.code in listOf("USD", "EUR", "CNY", "JPY")
    }

    fun getCurrencyByCode(code: String): Currency? = currencies.find { it.code == code }

    fun getLastUpdate(): String = "19.11.2024, 12:30"

    fun getHistoryForCurrency(code: String): List<CurrencyHistory> {
        val baseRate = getCurrencyByCode(code)?.cbRate ?: 100.0
        return listOf(
            CurrencyHistory("14.11", baseRate * 0.985),
            CurrencyHistory("15.11", baseRate * 0.992),
            CurrencyHistory("16.11", baseRate * 0.998),
            CurrencyHistory("17.11", baseRate * 1.002),
            CurrencyHistory("18.11", baseRate * 0.995),
            CurrencyHistory("19.11", baseRate)
        )
    }
}
