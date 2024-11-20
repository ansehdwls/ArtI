package com.hexa.arti.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.data.model.artmuseum.GalleryBanner
import com.hexa.arti.databinding.ItemArtMuseumBinding

class ArtMuseumAdapter(
    private val onItemClick: (GalleryBanner) -> Unit
) : ListAdapter<GalleryBanner, ArtMuseumAdapter.ArtMuseumViewHolder>(ArtMuseumDiffUtil()) {

    class ArtMuseumViewHolder(
        private val binding: ItemArtMuseumBinding,
        private val onItemClick: (GalleryBanner) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(galleryBanner: GalleryBanner) {

            Glide.with(binding.root.context)
                .load(galleryBanner.imageUrl)
                .error(R.drawable.gallery_example)
                .into(binding.ivArtMuseum)

            binding.tvArtTitle.text = galleryBanner.name

            itemView.setOnClickListener {
                onItemClick(galleryBanner)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtMuseumViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ArtMuseumViewHolder(
            ItemArtMuseumBinding.inflate(inflater, parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: ArtMuseumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


class ArtMuseumDiffUtil : DiffUtil.ItemCallback<GalleryBanner>() {
    override fun areItemsTheSame(oldItem: GalleryBanner, newItem: GalleryBanner): Boolean {
        return oldItem.galleryId == newItem.galleryId
    }

    override fun areContentsTheSame(oldItem: GalleryBanner, newItem: GalleryBanner): Boolean {
        return oldItem == newItem
    }
}
