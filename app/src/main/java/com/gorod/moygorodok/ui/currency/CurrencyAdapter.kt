package com.gorod.moygorodok.ui.currency

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Currency
import com.gorod.moygorodok.databinding.ItemCurrencyBinding
import java.text.DecimalFormat

class CurrencyAdapter : ListAdapter<Currency, CurrencyAdapter.CurrencyViewHolder>(CurrencyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = ItemCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CurrencyViewHolder(
        private val binding: ItemCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val rateFormat = DecimalFormat("#,##0.00")
        private val changeFormat = DecimalFormat("+#,##0.00;-#,##0.00")

        fun bind(currency: Currency) {
            binding.apply {
                textFlag.text = currency.flag
                textCode.text = currency.code
                textName.text = currency.name

                textBuyRate.text = rateFormat.format(currency.buyRate)
                textSellRate.text = rateFormat.format(currency.sellRate)
                textCbRate.text = rateFormat.format(currency.cbRate)

                // Change indicator
                val changeText = changeFormat.format(currency.change)
                textChange.text = changeText

                val changeColor = when {
                    currency.change > 0 -> "#4CAF50" // Green for increase
                    currency.change < 0 -> "#F44336" // Red for decrease
                    else -> "#757575" // Gray for no change
                }
                textChange.setTextColor(Color.parseColor(changeColor))

                // Change arrow
                val arrow = when {
                    currency.change > 0 -> "↑"
                    currency.change < 0 -> "↓"
                    else -> "→"
                }
                textArrow.text = arrow
                textArrow.setTextColor(Color.parseColor(changeColor))

                // Color indicator
                try {
                    colorIndicator.setBackgroundColor(Color.parseColor(currency.color))
                } catch (e: Exception) {
                    colorIndicator.setBackgroundColor(Color.parseColor("#2196F3"))
                }
            }
        }
    }

    class CurrencyDiffCallback : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem == newItem
        }
    }
}
