package com.gorod.moygorodok.ui.announcements

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.gorod.moygorodok.data.model.AnnouncementPhoto

class AnnouncementPhotoPagerAdapter(
    private val placeholderRes: Int
) : RecyclerView.Adapter<AnnouncementPhotoPagerAdapter.PhotoViewHolder>() {

    private val items = mutableListOf<AnnouncementPhoto>()

    fun submit(photos: List<AnnouncementPhoto>) {
        items.clear()
        items.addAll(photos)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val context = parent.context
        val imageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return PhotoViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = items[position]
        holder.imageView.load(photo.url) {
            placeholder(placeholderRes)
            error(placeholderRes)
            crossfade(true)
        }
    }

    class PhotoViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
