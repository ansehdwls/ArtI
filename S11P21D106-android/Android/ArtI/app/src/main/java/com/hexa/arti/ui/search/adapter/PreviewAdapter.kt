package com.hexa.arti.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.data.model.artmuseum.ThemeArtwork
import com.hexa.arti.databinding.ItemPreviewImageBinding

class PreviewAdapter(
    private val onImageClick: (ThemeArtwork) -> Unit,
) : ListAdapter<ThemeArtwork, PreviewAdapter.PreviewViewHolder>(PreviewDiffUtil()) {


    class PreviewViewHolder(
        private val binding: ItemPreviewImageBinding,
        private val onImageClick: (ThemeArtwork) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(previewImage: ThemeArtwork) {
            if (previewImage.isFocus) {
                binding.clPreviewImage.setBackgroundResource(R.drawable.background_preview_focus)
            } else {
                binding.clPreviewImage.setBackgroundResource(0)
            }
            Glide.with(binding.root.context)
                .load(previewImage.imageUrl)
                .error(R.drawable.gallery_sample2)
                .into(binding.ivPreviewImage)
            itemView.setOnClickListener {
                onImageClick(previewImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PreviewViewHolder(
            ItemPreviewImageBinding.inflate(inflater, parent, false),
            onImageClick
        )
    }

    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PreviewDiffUtil : DiffUtil.ItemCallback<ThemeArtwork>() {
    override fun areItemsTheSame(oldItem: ThemeArtwork, newItem: ThemeArtwork): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ThemeArtwork, newItem: ThemeArtwork): Boolean {
        return oldItem == newItem
    }

}