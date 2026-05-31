package com.gorod.moygorodok.ui.report.sheet

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.databinding.ItemReportScreenshotBinding

class ReportScreenshotAdapter(
    private val onRemove: (Int) -> Unit
) : ListAdapter<Uri, ReportScreenshotAdapter.Holder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemReportScreenshotBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class Holder(private val binding: ItemReportScreenshotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri, position: Int) {
            binding.imagePhoto.setImageURI(uri)
            binding.imageRemove.setOnClickListener { onRemove(position) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(a: Uri, b: Uri) = a == b
        override fun areContentsTheSame(a: Uri, b: Uri) = a == b
    }
}
