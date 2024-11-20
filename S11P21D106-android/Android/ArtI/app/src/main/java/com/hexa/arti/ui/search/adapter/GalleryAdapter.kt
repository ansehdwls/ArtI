package com.hexa.arti.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.data.model.response.GetSearchGalleryResponse
import com.hexa.arti.databinding.ItemArtMuseumBinding

class GalleryAdapter(
    private val onItemClick: (GetSearchGalleryResponse) -> Unit
) : ListAdapter<GetSearchGalleryResponse, GalleryAdapter.GalleryViewHolder>(GalleryDiffUtil()) {

    class GalleryViewHolder(
        private val binding: ItemArtMuseumBinding,
        private val onItemClick: (GetSearchGalleryResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gallery: GetSearchGalleryResponse) {

            Glide.with(binding.root.context)
                .load(gallery.image)
                .error(R.drawable.gallery_example)
                .into(binding.ivArtMuseum)

            binding.tvArtTitle.text = gallery.name

            itemView.setOnClickListener {
                onItemClick(gallery)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return GalleryViewHolder(
            ItemArtMuseumBinding.inflate(inflater, parent, false),
            onItemClick
        )
    }


    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


class GalleryDiffUtil : DiffUtil.ItemCallback<GetSearchGalleryResponse>() {
    override fun areItemsTheSame(
        oldItem: GetSearchGalleryResponse,
        newItem: GetSearchGalleryResponse
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: GetSearchGalleryResponse,
        newItem: GetSearchGalleryResponse
    ): Boolean {
        return oldItem == newItem
    }
}
