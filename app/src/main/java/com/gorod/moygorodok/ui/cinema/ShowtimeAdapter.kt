package com.gorod.moygorodok.ui.cinema

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Showtime
import com.gorod.moygorodok.databinding.ItemShowtimeBinding

class ShowtimeAdapter(
    private val onShowtimeClick: (Showtime) -> Unit
) : ListAdapter<Showtime, ShowtimeAdapter.ShowtimeViewHolder>(ShowtimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val binding = ItemShowtimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShowtimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShowtimeViewHolder(
        private val binding: ItemShowtimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(showtime: Showtime) {
            binding.apply {
                textTime.text = showtime.time
                textCinema.text = showtime.cinema.name
                textHall.text = showtime.hall
                textFormat.text = showtime.format
                textPrice.text = "${showtime.price} ₽"
                textSeats.text = "${showtime.availableSeats} мест"

                // Set cinema indicator color
                try {
                    cinemaIndicator.setBackgroundColor(Color.parseColor(showtime.cinema.color))
                } catch (e: Exception) {
                    cinemaIndicator.setBackgroundColor(Color.parseColor("#2196F3"))
                }

                // Format badge color
                val formatColor = when (showtime.format) {
                    "IMAX" -> "#FF5722"
                    "3D" -> "#2196F3"
                    else -> "#4CAF50"
                }
                try {
                    textFormat.setTextColor(Color.parseColor(formatColor))
                } catch (e: Exception) {
                    textFormat.setTextColor(Color.parseColor("#4CAF50"))
                }

                root.setOnClickListener {
                    onShowtimeClick(showtime)
                }
            }
        }
    }

    class ShowtimeDiffCallback : DiffUtil.ItemCallback<Showtime>() {
        override fun areItemsTheSame(oldItem: Showtime, newItem: Showtime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Showtime, newItem: Showtime): Boolean {
            return oldItem == newItem
        }
    }
}
