package com.gorod.moygorodok.ui.company.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.gorod.moygorodok.data.model.CompanyPhoto
import com.gorod.moygorodok.databinding.ItemCompanyPhotoBinding

class CompanyPhotoAdapter(
    private val items: List<CompanyPhoto>
) : RecyclerView.Adapter<CompanyPhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemCompanyPhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class PhotoViewHolder(
        private val binding: ItemCompanyPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: CompanyPhoto) {
            binding.imagePhoto.load(photo.thumbnailUrl ?: photo.url)
        }
    }
}
