package com.hexa.arti.ui.artGalleryDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexa.arti.databinding.ItemGalleryMenuThemeBinding
import com.hexa.arti.databinding.ItemThemeBinding

class GalleryThemeMenuAdapter(
    val themes : List<String>,
    private val onMenuClick: (Int) -> Unit,
): RecyclerView.Adapter<GalleryThemeMenuAdapter.ThemeViewHolder>() {

    inner class ThemeViewHolder(private val binding: ItemGalleryMenuThemeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(theme: String) {
            binding.galleryMenuThemeTv.text = theme
            binding.galleryMenuThemeTv.setOnClickListener {
                onMenuClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val binding = ItemGalleryMenuThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(themes[position])
    }

    override fun getItemCount(): Int = themes.size
}