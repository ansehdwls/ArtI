package com.hexa.arti.ui.search.paging

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hexa.arti.R
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.databinding.ItemArtBinding
import com.hexa.arti.ui.search.adapter.ArtDiffUtil

class ArtworkPagingAdapter(
    private val onItemClick: (Artwork) -> Unit
) : PagingDataAdapter<Artwork, ArtworkPagingAdapter.ArtViewHolder>(ArtDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ArtViewHolder(
            ItemArtBinding.inflate(inflater, parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ArtViewHolder(
        private val binding: ItemArtBinding,
        private val onItemClick: (Artwork) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(artwork: Artwork) {
            val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            Glide.with(binding.root.context)
                .load(artwork.imageUrl)
                .placeholder(circularProgressDrawable)
                .override(200, 200)
                .into(binding.ivArt)

            binding.tvArtTitle.text = artwork.title

            itemView.setOnClickListener {
                Log.d("확안","작품 확인 $artwork")
                onItemClick(artwork)
            }
        }
    }
}