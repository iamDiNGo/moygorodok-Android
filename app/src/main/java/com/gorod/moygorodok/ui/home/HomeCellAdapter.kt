package com.gorod.moygorodok.ui.home

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.remote.model.HomeCellDto
import com.gorod.moygorodok.databinding.ItemHomeCellBinding

class HomeCellAdapter(
    private val onCellClick: (HomeCellDto) -> Unit
) : ListAdapter<HomeCellDto, HomeCellAdapter.CellViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val binding = ItemHomeCellBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CellViewHolder(binding, onCellClick)
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CellViewHolder(
        private val binding: ItemHomeCellBinding,
        private val onCellClick: (HomeCellDto) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cell: HomeCellDto) {
            binding.apply {
                textTitle.text = cell.content.title
                textTitle.setTextColor(Color.parseColor(cell.style.textColor))

                if (!cell.content.subtitle.isNullOrEmpty()) {
                    textSubtitle.text = cell.content.subtitle
                    textSubtitle.setTextColor(Color.parseColor(cell.style.textColor))
                    textSubtitle.visibility = View.VISIBLE
                } else {
                    textSubtitle.visibility = View.GONE
                }

                val textColor = Color.parseColor(cell.style.textColor)
                cardCell.setCardBackgroundColor(Color.parseColor(cell.style.background))
                iconArrow.colorFilter = PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_IN)

                root.setOnClickListener { onCellClick(cell) }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<HomeCellDto>() {
        override fun areItemsTheSame(oldItem: HomeCellDto, newItem: HomeCellDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeCellDto, newItem: HomeCellDto): Boolean {
            return oldItem == newItem
        }
    }
}
