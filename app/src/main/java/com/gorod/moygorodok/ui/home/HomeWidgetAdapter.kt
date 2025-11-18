package com.gorod.moygorodok.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.HomeWidget
import com.gorod.moygorodok.databinding.ItemWidgetAdsBinding
import com.gorod.moygorodok.databinding.ItemWidgetDeliveryBinding
import com.gorod.moygorodok.databinding.ItemWidgetNewsBinding
import com.gorod.moygorodok.databinding.ItemWidgetWeatherBinding
import java.text.NumberFormat
import java.util.Locale

class HomeWidgetAdapter(
    private val onWeatherClick: () -> Unit,
    private val onNewsClick: () -> Unit,
    private val onAdsClick: () -> Unit,
    private val onDeliveryClick: () -> Unit
) : ListAdapter<HomeWidget, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_WEATHER = 0
        private const val TYPE_NEWS = 1
        private const val TYPE_ADS = 2
        private const val TYPE_DELIVERY = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeWidget.WeatherWidget -> TYPE_WEATHER
            is HomeWidget.NewsWidget -> TYPE_NEWS
            is HomeWidget.AdsWidget -> TYPE_ADS
            is HomeWidget.DeliveryWidget -> TYPE_DELIVERY
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
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeWidget.WeatherWidget -> (holder as WeatherViewHolder).bind(item)
            is HomeWidget.NewsWidget -> (holder as NewsViewHolder).bind(item)
            is HomeWidget.AdsWidget -> (holder as AdsViewHolder).bind(item)
            is HomeWidget.DeliveryWidget -> (holder as DeliveryViewHolder).bind(item)
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

    private class DiffCallback : DiffUtil.ItemCallback<HomeWidget>() {
        override fun areItemsTheSame(oldItem: HomeWidget, newItem: HomeWidget): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: HomeWidget, newItem: HomeWidget): Boolean {
            return oldItem == newItem
        }
    }
}
