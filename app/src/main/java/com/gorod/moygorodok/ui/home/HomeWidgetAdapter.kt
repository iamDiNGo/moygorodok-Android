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
import com.gorod.moygorodok.databinding.ItemWidgetDeliveryBinding
import com.gorod.moygorodok.databinding.ItemWidgetEmergencyBinding
import com.gorod.moygorodok.databinding.ItemWidgetNewsBinding
import com.gorod.moygorodok.databinding.ItemWidgetTasksBinding
import com.gorod.moygorodok.databinding.ItemWidgetWeatherBinding
import java.text.NumberFormat
import java.util.Locale

class HomeWidgetAdapter(
    private val onWeatherClick: () -> Unit,
    private val onNewsClick: () -> Unit,
    private val onAdsClick: () -> Unit,
    private val onDeliveryClick: () -> Unit,
    private val onTasksClick: () -> Unit,
    private val onAdminClick: () -> Unit,
    private val onEmergencyClick: () -> Unit
) : ListAdapter<HomeWidget, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_WEATHER = 0
        private const val TYPE_NEWS = 1
        private const val TYPE_ADS = 2
        private const val TYPE_DELIVERY = 3
        private const val TYPE_TASKS = 4
        private const val TYPE_ADMIN = 5
        private const val TYPE_EMERGENCY = 6
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeWidget.WeatherWidget -> TYPE_WEATHER
            is HomeWidget.NewsWidget -> TYPE_NEWS
            is HomeWidget.AdsWidget -> TYPE_ADS
            is HomeWidget.DeliveryWidget -> TYPE_DELIVERY
            is HomeWidget.TasksWidget -> TYPE_TASKS
            is HomeWidget.AdminWidget -> TYPE_ADMIN
            is HomeWidget.EmergencyWidget -> TYPE_EMERGENCY
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
            TYPE_ADS -> AdsViewHolder(
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
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeWidget.WeatherWidget -> (holder as WeatherViewHolder).bind(item)
            is HomeWidget.NewsWidget -> (holder as NewsViewHolder).bind(item)
            is HomeWidget.AdsWidget -> (holder as AdsViewHolder).bind(item)
            is HomeWidget.DeliveryWidget -> (holder as DeliveryViewHolder).bind(item)
            is HomeWidget.TasksWidget -> (holder as TasksViewHolder).bind(item)
            is HomeWidget.AdminWidget -> (holder as AdminViewHolder).bind(item)
            is HomeWidget.EmergencyWidget -> (holder as EmergencyViewHolder).bind(item)
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

    class AdsViewHolder(
        private val binding: ItemWidgetAdsBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeWidget.AdsWidget) {
            binding.apply {
                textTitle.text = item.title
                textCount.text = "${item.adsCount} объявлений"

                // Show latest ads
                if (item.latestAds.isNotEmpty()) {
                    val ad1 = item.latestAds[0]
                    textAd1Title.text = ad1.title
                    textAd1Price.text = formatPrice(ad1.price)
                    layoutAd1.visibility = android.view.View.VISIBLE
                }
                if (item.latestAds.size > 1) {
                    val ad2 = item.latestAds[1]
                    textAd2Title.text = ad2.title
                    textAd2Price.text = formatPrice(ad2.price)
                    layoutAd2.visibility = android.view.View.VISIBLE
                }
                if (item.latestAds.size > 2) {
                    val ad3 = item.latestAds[2]
                    textAd3Title.text = ad3.title
                    textAd3Price.text = formatPrice(ad3.price)
                    layoutAd3.visibility = android.view.View.VISIBLE
                }

                root.setOnClickListener { onClick() }
            }
        }

        private fun formatPrice(price: Double): String {
            return if (price > 0) {
                val format = NumberFormat.getNumberInstance(Locale("ru", "RU"))
                "${format.format(price)} ₽"
            } else {
                "Бесплатно"
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

    private class DiffCallback : DiffUtil.ItemCallback<HomeWidget>() {
        override fun areItemsTheSame(oldItem: HomeWidget, newItem: HomeWidget): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: HomeWidget, newItem: HomeWidget): Boolean {
            return oldItem == newItem
        }
    }
}
