package com.hexa.arti.ui.search.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.databinding.ItemArtBinding

class ArtworkAdapter(
    private val onItemClick: (Artwork) -> Unit
) : ListAdapter<Artwork, ArtworkAdapter.ArtViewHolder>(ArtDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ArtViewHolder(
            ItemArtBinding.inflate(inflater, parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ArtViewHolder(
        private val binding: ItemArtBinding,
        private val onItemClick: (Artwork) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(artwork: Artwork) {

//            val requestOptions = RequestOptions()
//                .downsample(DownsampleStrategy.AT_MOST)
//                .placeholder(R.drawable.gallery_example)
//                .error(R.drawable.gallery_example)

//            Glide.with(binding.root.context)
//                .load(artwork.imageUrl)
//                .error(R.drawable.gallery_example)
//                .into(binding.ivArt)

            val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            val startTime = System.currentTimeMillis()

            Glide.with(binding.root.context)
                .load(artwork.imageUrl)
                .placeholder(circularProgressDrawable)
                .override(240, 167)
                .into(binding.ivArt)

            val endTime = System.currentTimeMillis()
            val executionTime = endTime - startTime
            
            Log.d("확인","이미지 로딩 속도 확인 $executionTime")
            
            binding.tvArtTitle.text = artwork.title

            itemView.setOnClickListener {
                onItemClick(artwork)
            }
        }
    }
}

class ArtDiffUtil : DiffUtil.ItemCallback<Artwork>() {
    override fun areItemsTheSame(oldItem: Artwork, newItem: Artwork): Boolean {
        return oldItem.artworkId == newItem.artworkId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Artwork, newItem: Artwork): Boolean {
        return oldItem == newItem
    }
}