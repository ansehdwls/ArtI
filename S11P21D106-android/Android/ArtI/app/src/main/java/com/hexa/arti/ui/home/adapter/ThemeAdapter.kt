package com.hexa.arti.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.data.model.home.HomeTheme
import com.hexa.arti.databinding.ItemThemeBinding

class ThemeAdapter : ListAdapter<HomeTheme, ThemeAdapter.ThemeViewHolder>(ThemeDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ThemeViewHolder(ItemThemeBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ThemeViewHolder(
        private val binding: ItemThemeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(homeTheme: HomeTheme) {
            binding.tvThemeTitle.text = "테마 : ${homeTheme.themeName}"

            if (homeTheme.artworks.size >= 1) {
                Glide.with(binding.root.context)
                    .load(homeTheme.artworks[0].imageUrl)
                    .error(R.drawable.temp_preview_image1)
                    .into(binding.ivPreviewImage1)
                binding.ivPreviewImage1.visibility = View.VISIBLE
            }
            if (homeTheme.artworks.size >= 2) {
                Glide.with(binding.root.context)
                    .load(homeTheme.artworks[1].imageUrl)
                    .error(R.drawable.temp_preview_image2)
                    .into(binding.ivPreviewImage2)
                binding.ivPreviewImage2.visibility = View.VISIBLE
            }
            if (homeTheme.artworks.size >= 3) {
                Glide.with(binding.root.context)
                    .load(homeTheme.artworks[2].imageUrl)
                    .error(R.drawable.temp_preview_image3)
                    .into(binding.ivPreviewImage3)
                binding.ivPreviewImage3.visibility = View.VISIBLE
            }

        }
    }
}

class ThemeDiffUtil : DiffUtil.ItemCallback<HomeTheme>() {
    override fun areItemsTheSame(oldItem: HomeTheme, newItem: HomeTheme): Boolean {
        return oldItem.themeId == newItem.themeId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: HomeTheme, newItem: HomeTheme): Boolean {
        return oldItem == newItem
    }
}