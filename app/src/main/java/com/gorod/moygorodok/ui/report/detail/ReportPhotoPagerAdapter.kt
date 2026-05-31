package com.gorod.moygorodok.ui.report.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.gorod.moygorodok.data.model.ReportPhoto
import com.gorod.moygorodok.databinding.ItemReportPhotoBinding

class ReportPhotoPagerAdapter(
    private val photos: List<ReportPhoto>
) : RecyclerView.Adapter<ReportPhotoPagerAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemReportPhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(binding)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(photos[position])
    }

    class Holder(private val binding: ItemReportPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: ReportPhoto) {
            binding.progressBar.visibility = android.view.View.VISIBLE
            binding.imagePhoto.load(photo.url) {
                crossfade(true)
                listener(
                    onSuccess = { _, _ -> binding.progressBar.visibility = android.view.View.GONE },
                    onError = { _, _ -> binding.progressBar.visibility = android.view.View.GONE }
                )
            }
        }
    }
}
