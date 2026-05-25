package com.gorod.moygorodok.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.HomeWidget
import com.gorod.moygorodok.data.model.TaskPriceType
import com.gorod.moygorodok.databinding.ItemWidgetAdminBinding
import com.gorod.moygorodok.databinding.ItemWidgetAdsBinding
import android.view.View
import com.gorod.moygorodok.databinding.ItemWidgetDeliveryBinding
import com.gorod.moygorodok.databinding.ItemWidgetChatBinding
import com.gorod.moygorodok.databinding.ItemWidgetCinemaBinding
import com.gorod.moygorodok.databinding.ItemWidgetCurrencyBinding
import com.gorod.moygorodok.databinding.ItemWidgetCompanyBinding
import com.gorod.moygorodok.databinding.ItemWidgetEmergencyBinding
import com.gorod.moygorodok.databinding.ItemWidgetHoroscopeBinding
import com.gorod.moygorodok.databinding.ItemWidgetNewsBinding
import com.gorod.moygorodok.databinding.ItemWidgetNotificationsBinding
import com.gorod.moygorodok.databinding.ItemWidgetTasksBinding
import com.gorod.moygorodok.databinding.ItemWidgetWeatherBinding
import java.util.Locale

class HomeWidgetAdapter(
    private val onWeatherClick: () -> Unit,
    private val onNewsClick: () -> Unit,
    private val onAdsClick: () -> Unit,
    private val onDeliveryClick: () -> Unit,
    private val onTasksClick: () -> Unit,
    private val onAdminClick: () -> Unit,
    private val onEmergencyClick: () -> Unit,
    private val onNotificationsClick: () -> Unit,
    private val onChatClick: () -> Unit,
    private val onCinemaClick: () -> Unit,
    private val onCurrencyClick: () -> Unit,
    private val onCompanyClick: () -> Unit,
    private val onHoroscopeClick: (HomeWidget.HoroscopeWidget) -> Unit
) : ListAdapter<HomeWidget, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_WEATHER = 0
        private const val TYPE_NEWS = 1
        private const val TYPE_ADS = 2
        private const val TYPE_DELIVERY = 3
        private const val TYPE_TASKS = 4
        private const val TYPE_ADMIN = 5
        private const val TYPE_EMERGENCY = 6
        private const val TYPE_NOTIFICATIONS = 7
        private const val TYPE_CHAT = 8
        private const val TYPE_CINEMA = 9
        private const val TYPE_CURRENCY = 10
        private const val TYPE_COMPANY = 11
        private const val TYPE_HOROSCOPE = 12
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeWidget.WeatherWidget -> TYPE_WEATHER
            is HomeWidget.NewsWidget -> TYPE_NEWS
            is HomeWidget.AnnouncementsWidget -> TYPE_ADS
            is HomeWidget.DeliveryWidget -> TYPE_DELIVERY
            is HomeWidget.TasksWidget -> TYPE_TASKS
            is HomeWidget.AdminWidget -> TYPE_ADMIN
            is HomeWidget.EmergencyWidget -> TYPE_EMERGENCY
            is HomeWidget.NotificationsWidget -> TYPE_NOTIFICATIONS
            is HomeWidget.ChatWidget -> TYPE_CHAT
            is HomeWidget.CinemaWidget -> TYPE_CINEMA
            is HomeWidget.CurrencyWidget -> TYPE_CURRENCY
            is HomeWidget.CompanyWidget -> TYPE_COMPANY
            is HomeWidget.HoroscopeWidget -> TYPE_HOROSCOPE
            else -> throw IllegalArgumentException("Unknown widget type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WEATHER -> WeatherViewHolder(
                ItemWidgetWeatherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onWeatherClick
            )
            TYPE_NEWS -> NewsViewHolder(
                ItemWidgetNewsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onNewsClick
            )
            TYPE_ADS -> AnnouncementsViewHolder(
                ItemWidgetAdsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onAdsClick
            )
            TYPE_DELIVERY -> DeliveryViewHolder(
                ItemWidgetDeliveryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onDeliveryClick
            )
            TYPE_TASKS -> TasksViewHolder(
                ItemWidgetTasksBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onTasksClick
            )
            TYPE_ADMIN -> AdminViewHolder(
                ItemWidgetAdminBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onAdminClick
            )
            TYPE_EMERGENCY -> EmergencyViewHolder(
                ItemWidgetEmergencyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onEmergencyClick
            )
            TYPE_NOTIFICATIONS -> NotificationsViewHolder(
                ItemWidgetNotificationsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onNotificationsClick
            )
            TYPE_CHAT -> ChatViewHolder(
                ItemWidgetChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onChatClick
            )
            TYPE_CINEMA -> CinemaViewHolder(
                ItemWidgetCinemaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onCinemaClick
            )
            TYPE_CURRENCY -> CurrencyViewHolder(
                ItemWidgetCurrencyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onCurrencyClick
            )
            TYPE_COMPANY -> CompanyViewHolder(
                ItemWidgetCompanyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onCompanyClick
            )
            TYPE_HOROSCOPE -> HoroscopeViewHolder(
                ItemWidgetHoroscopeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onHoroscopeClick
            )
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeWidget.WeatherWidget -> (holder as WeatherViewHolder).bind(item)
            is HomeWidget.NewsWidget -> (holder as NewsViewHolder).bind(item)
            is HomeWidget.AnnouncementsWidget -> (holder as AnnouncementsViewHolder).bind(item)
            is HomeWidget.DeliveryWidget -> (holder as DeliveryViewHolder).bind(item)
            is HomeWidget.TasksWidget -> (holder as TasksViewHolder).bind(item)
            is HomeWidget.AdminWidget -> (holder as AdminViewHolder).bind(item)
            is HomeWidget.EmergencyWidget -> (holder as EmergencyViewHolder).bind(item)
            is HomeWidget.NotificationsWidget -> (holder as NotificationsViewHolder).bind(item)
            is HomeWidget.ChatWidget -> (holder as ChatViewHolder).bind(item)
            is HomeWidget.CinemaWidget -> (holder as CinemaViewHolder).bind(item)
            is HomeWidget.CurrencyWidget -> (holder as CurrencyViewHolder).bind(item)
            is HomeWidget.CompanyWidget -> (holder as CompanyViewHolder).bind(item)
            is HomeWidget.HoroscopeWidget -> (holder as HoroscopeViewHolder).bind(item)
            else -> {}
        }
    }

    class WeatherViewHolder(
        private val binding: ItemWidgetWeatherBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.WeatherWidget) {
            binding.apply {
                textLocation.text = item.location
                textTemp.text = "${item.currentTemp}°"
                textCondition.text = item.condition.displayName
                textIcon.text = item.condition.icon
                textHighLow.text = "Макс: ${item.highTemp}°  Мин: ${item.lowTemp}°"

                root.setOnClickListener { onClick() }
            }
        }
    }

    class NewsViewHolder(
        private val binding: ItemWidgetNewsBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.NewsWidget) {
            binding.apply {
                textTitle.text = item.title
                textCount.text = "${item.newsCount} новостей"

                // Show latest news items
                if (item.latestNews.isNotEmpty()) {
                    textNews1.text = item.latestNews[0].title
                    textNews1.visibility = android.view.View.VISIBLE
                }
                if (item.latestNews.size > 1) {
                    textNews2.text = item.latestNews[1].title
                    textNews2.visibility = android.view.View.VISIBLE
                }
                if (item.latestNews.size > 2) {
                    textNews3.text = item.latestNews[2].title
                    textNews3.visibility = android.view.View.VISIBLE
                }

                root.setOnClickListener { onClick() }
            }
        }
    }

    class AnnouncementsViewHolder(
        private val binding: ItemWidgetAdsBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.AnnouncementsWidget) {
            binding.apply {
                textTitle.text = item.title
                textCount.text = "${item.totalCount} объявлений"

                val rows = listOf(
                    Triple(layoutAd1, textAd1Title, textAd1Price),
                    Triple(layoutAd2, textAd2Title, textAd2Price),
                    Triple(layoutAd3, textAd3Title, textAd3Price)
                )
                rows.forEachIndexed { index, (layout, title, price) ->
                    val announcement = item.items.getOrNull(index)
                    if (announcement == null) {
                        layout.visibility = View.GONE
                    } else {
                        layout.visibility = View.VISIBLE
                        title.text = announcement.title
                        price.text = announcement.priceFormatted
                    }
                }

                root.setOnClickListener { onClick() }
            }
        }
    }

    class DeliveryViewHolder(
        private val binding: ItemWidgetDeliveryBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.DeliveryWidget) {
            binding.apply {
                textTitle.text = item.title
                textCount.text = "${item.deliveryCount} заведений"

                // Show latest deliveries
                if (item.latestDeliveries.isNotEmpty()) {
                    val d1 = item.latestDeliveries[0]
                    textDelivery1Name.text = d1.name
                    textDelivery1Rating.text = "★ ${d1.rating}"
                    layoutDelivery1.visibility = android.view.View.VISIBLE
                }
                if (item.latestDeliveries.size > 1) {
                    val d2 = item.latestDeliveries[1]
                    textDelivery2Name.text = d2.name
                    textDelivery2Rating.text = "★ ${d2.rating}"
                    layoutDelivery2.visibility = android.view.View.VISIBLE
                }
                if (item.latestDeliveries.size > 2) {
                    val d3 = item.latestDeliveries[2]
                    textDelivery3Name.text = d3.name
                    textDelivery3Rating.text = "★ ${d3.rating}"
                    layoutDelivery3.visibility = android.view.View.VISIBLE
                }

                root.setOnClickListener { onClick() }
            }
        }
    }

    class TasksViewHolder(
        private val binding: ItemWidgetTasksBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.TasksWidget) {
            binding.apply {
                textTitle.text = item.title
                textCount.text = "${item.taskCount}"

                // Show latest tasks
                if (item.latestTasks.isNotEmpty()) {
                    val t1 = item.latestTasks[0]
                    task1Title.text = t1.title
                    task1Price.text = formatTaskPrice(t1)
                    task1Urgent.visibility = if (t1.isUrgent) android.view.View.VISIBLE else android.view.View.GONE
                    task1.visibility = android.view.View.VISIBLE
                }
                if (item.latestTasks.size > 1) {
                    val t2 = item.latestTasks[1]
                    task2Title.text = t2.title
                    task2Price.text = formatTaskPrice(t2)
                    task2Urgent.visibility = if (t2.isUrgent) android.view.View.VISIBLE else android.view.View.GONE
                    task2.visibility = android.view.View.VISIBLE
                }
                if (item.latestTasks.size > 2) {
                    val t3 = item.latestTasks[2]
                    task3Title.text = t3.title
                    task3Price.text = formatTaskPrice(t3)
                    task3Urgent.visibility = if (t3.isUrgent) android.view.View.VISIBLE else android.view.View.GONE
                    task3.visibility = android.view.View.VISIBLE
                }

                root.setOnClickListener { onClick() }
            }
        }

        private fun formatTaskPrice(task: com.gorod.moygorodok.data.model.Task): String {
            return when (task.price.type) {
                TaskPriceType.FIXED -> "${task.price.amount?.toInt()} ₽"
                TaskPriceType.NEGOTIABLE -> "Договорная"
                TaskPriceType.HOURLY -> "${task.price.amount?.toInt()} ₽/час"
                TaskPriceType.RANGE -> "${task.price.amount?.toInt()} - ${task.price.maxAmount?.toInt()} ₽"
            }
        }
    }

    class AdminViewHolder(
        private val binding: ItemWidgetAdminBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.AdminWidget) {
            binding.apply {
                textDeliveryName.text = item.deliveryName
                textOrders.text = item.todayOrders.toString()
                textRevenue.text = item.todayRevenue
                textStatus.text = if (item.isOpen) "Открыто" else "Закрыто"
                textStatus.setBackgroundResource(
                    if (item.isOpen) com.gorod.moygorodok.R.drawable.bg_status_badge
                    else com.gorod.moygorodok.R.drawable.bg_badge_closed
                )

                root.setOnClickListener { onClick() }
            }
        }
    }

    class EmergencyViewHolder(
        private val binding: ItemWidgetEmergencyBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.EmergencyWidget) {
            binding.root.setOnClickListener { onClick() }
        }
    }

    class NotificationsViewHolder(
        private val binding: ItemWidgetNotificationsBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.NotificationsWidget) {
            binding.apply {
                textBadge.text = item.unreadCount.toString()
                textBadge.visibility = if (item.unreadCount > 0) android.view.View.VISIBLE else android.view.View.GONE
                root.setOnClickListener { onClick() }
            }
        }
    }

    class ChatViewHolder(
        private val binding: ItemWidgetChatBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.ChatWidget) {
            binding.apply {
                textOnline.text = "${item.onlineCount} онлайн"
                root.setOnClickListener { onClick() }
            }
        }
    }

    class CinemaViewHolder(
        private val binding: ItemWidgetCinemaBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.CinemaWidget) {
            binding.apply {
                textNowPlaying.text = item.nowPlayingCount.toString()
                textCinemasCount.text = item.cinemasCount.toString()
                textUpcoming.text = "Скоро: ${item.upcomingMovies.joinToString(", ")}"
                root.setOnClickListener { onClick() }
            }
        }
    }

    class CurrencyViewHolder(
        private val binding: ItemWidgetCurrencyBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.CurrencyWidget) {
            binding.apply {
                textUsdRate.text = formatRate(item.usdRate)
                textEurRate.text = formatRate(item.eurRate)
                textCnyRate.text = formatRate(item.cnyRate)
                textJpyRate.text = formatRate(item.jpyRate)
                textLastUpdate.text = if (item.lastUpdate.isBlank()) {
                    "Курсы валют"
                } else {
                    "Обновлено: ${item.lastUpdate}"
                }
                root.setOnClickListener { onClick() }
            }
        }

        private fun formatRate(value: Double?): String =
            value?.let { String.format(Locale.US, "%.2f", it) } ?: "—"
    }

    class CompanyViewHolder(
        private val binding: ItemWidgetCompanyBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.CompanyWidget) {
            binding.apply {
                textTotal.text = item.totalCount.toString()
                textVerified.text = item.verifiedCount.toString()
                textCategories.text = item.categoriesCount.toString()
                root.setOnClickListener { onClick() }
            }
        }
    }

    class HoroscopeViewHolder(
        private val binding: ItemWidgetHoroscopeBinding,
        private val onClick: (HomeWidget.HoroscopeWidget) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.HoroscopeWidget) {
            binding.apply {
                when (val state = item.state) {
                    is com.gorod.moygorodok.data.model.HoroscopeWidgetState.Ready -> {
                        textHoroscopeSymbol.text = state.symbol.ifBlank { "✨" }
                        textHoroscopeTitle.text = buildString {
                            append(state.zodiacSignLabel)
                            state.date?.let { append(" · ").append(formatHoroscopeDate(it)) }
                        }
                        textHoroscopeBody.text = state.text
                    }
                    com.gorod.moygorodok.data.model.HoroscopeWidgetState.Anonymous -> {
                        textHoroscopeSymbol.text = "✨"
                        textHoroscopeTitle.text = "Гороскоп"
                        textHoroscopeBody.text = "Войдите, чтобы видеть персональный гороскоп"
                    }
                    com.gorod.moygorodok.data.model.HoroscopeWidgetState.NoBirthday -> {
                        textHoroscopeSymbol.text = "✨"
                        textHoroscopeTitle.text = "Гороскоп"
                        textHoroscopeBody.text = "Укажите дату рождения в профиле"
                    }
                    com.gorod.moygorodok.data.model.HoroscopeWidgetState.Empty -> {
                        textHoroscopeSymbol.text = "✨"
                        textHoroscopeTitle.text = "Гороскоп"
                        textHoroscopeBody.text = "Сегодня прогноза нет, загляните позже"
                    }
                }
                root.setOnClickListener { onClick(item) }
            }
        }

        private fun formatHoroscopeDate(raw: String): String {
            return try {
                val parser = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ROOT)
                val formatter = java.text.SimpleDateFormat("d MMMM", java.util.Locale("ru", "RU"))
                parser.parse(raw)?.let(formatter::format) ?: raw
            } catch (e: Exception) {
                raw
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<HomeWidget>() {
        override fun areItemsTheSame(oldItem: HomeWidget, newItem: HomeWidget): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: HomeWidget, newItem: HomeWidget): Boolean {
            return oldItem == newItem
        }
    }
}
