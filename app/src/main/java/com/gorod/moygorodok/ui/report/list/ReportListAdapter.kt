package com.gorod.moygorodok.ui.report.list

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Report
import com.gorod.moygorodok.databinding.ItemReportBinding
import com.gorod.moygorodok.ui.report.colorResFor
import com.gorod.moygorodok.ui.report.iconResFor
import com.gorod.moygorodok.util.ReportDateFormatter

class ReportListAdapter(
    private val onClick: (Report) -> Unit
) : ListAdapter<Report, ReportListAdapter.Holder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class Holder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report) {
            val ctx = binding.root.context

            binding.imageIcon.setImageResource(iconResFor(report.reason))
            binding.textPreview.text = report.reportablePreview?.takeIf { it.isNotBlank() }
                ?: report.reportableType.apiValue
            binding.textReason.text = report.reasonLabel
            binding.textDate.text = ReportDateFormatter.formatShort(report.createdAt)

            binding.chipStatus.text = report.statusLabel
            val color = ContextCompat.getColor(ctx, colorResFor(report.status))
            binding.chipStatus.chipBackgroundColor = ColorStateList.valueOf(color)
            binding.chipStatus.setTextColor(android.graphics.Color.WHITE)

            binding.root.setOnClickListener { onClick(report) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Report>() {
        override fun areItemsTheSame(a: Report, b: Report) = a.id == b.id
        override fun areContentsTheSame(a: Report, b: Report) = a == b
    }
}
