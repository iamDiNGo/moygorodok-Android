package com.gorod.moygorodok.ui.report.sheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.ReportReason
import com.gorod.moygorodok.databinding.ItemReportReasonBinding
import com.gorod.moygorodok.ui.report.iconResFor

class ReportReasonAdapter(
    private val onClick: (ReportReason) -> Unit
) : ListAdapter<ReportReason, ReportReasonAdapter.Holder>(Diff) {

    private var selected: ReportReason? = null

    fun setSelected(reason: ReportReason?) {
        val previous = selected
        selected = reason
        currentList.forEachIndexed { index, item ->
            if (item == previous || item == reason) notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemReportReasonBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val reason = getItem(position)
        holder.bind(reason, reason == selected)
    }

    inner class Holder(private val binding: ItemReportReasonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reason: ReportReason, isSelected: Boolean) {
            binding.imageIcon.setImageResource(iconResFor(reason))
            binding.textTitle.text = reason.label
            binding.imageCheck.setImageResource(
                if (isSelected) R.drawable.ic_arrow_right else R.drawable.ic_arrow_right
            )
            binding.imageCheck.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
            binding.root.setOnClickListener { onClick(reason) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<ReportReason>() {
        override fun areItemsTheSame(a: ReportReason, b: ReportReason) = a == b
        override fun areContentsTheSame(a: ReportReason, b: ReportReason) = a == b
    }
}
