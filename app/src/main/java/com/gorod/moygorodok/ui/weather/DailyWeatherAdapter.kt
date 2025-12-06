package com.gorod.moygorodok.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.DailyWeather
import com.gorod.moygorodok.databinding.ItemDailyWeatherBinding

class DailyWeatherAdapter : ListAdapter<DailyWeather, DailyWeatherAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDailyWeatherBinding.inflate(
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
        private val binding: ItemDailyWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DailyWeather) {
            binding.textDay.text = item.dayOfWeek
            binding.textIcon.text = item.condition.icon
            binding.textHighTemp.text = "${item.highTemp}°"
            binding.textLowTemp.text = "${item.lowTemp}°"

            if (item.precipProbability > 30) {
                binding.textPrecip.text = "${item.precipProbability}%"
                binding.textPrecip.visibility = android.view.View.VISIBLE
            } else {
                binding.textPrecip.visibility = android.view.View.GONE
            }

            // Temperature bar visualization
            val tempRange = 30 // -15 to +15 range
            val lowProgress = ((item.lowTemp + 15) * 100 / tempRange).coerceIn(0, 100)
            val highProgress = ((item.highTemp + 15) * 100 / tempRange).coerceIn(0, 100)

            val layoutParams = binding.viewTempBar.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = (lowProgress * binding.root.context.resources.displayMetrics.density).toInt()
            binding.viewTempBar.layoutParams = layoutParams
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<DailyWeather>() {
        override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem == newItem
        }
    }
}
