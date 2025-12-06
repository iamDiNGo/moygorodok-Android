package com.gorod.moygorodok.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.HourlyWeather
import com.gorod.moygorodok.databinding.ItemHourlyWeatherBinding

class HourlyWeatherAdapter : ListAdapter<HourlyWeather, HourlyWeatherAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHourlyWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemHourlyWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HourlyWeather) {
            binding.textHour.text = item.hour
            binding.textIcon.text = item.condition.icon
            binding.textTemp.text = "${item.temp}°"

            if (item.precipProbability > 30) {
                binding.textPrecip.text = "${item.precipProbability}%"
                binding.textPrecip.visibility = android.view.View.VISIBLE
            } else {
                binding.textPrecip.visibility = android.view.View.GONE
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<HourlyWeather>() {
        override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem.hour == newItem.hour
        }

        override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem == newItem
        }
    }
}
