package com.hexa.arti.ui.artGalleryDetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.data.model.artmuseum.ThemeArtworksResponseItem

class GalleryDetailViewPagerAdapter(
    private val imageUrlList: List<ThemeArtworksResponseItem>,
    private val onImgClick: (ThemeArtworksResponseItem) -> Unit,
) : RecyclerView.Adapter<GalleryDetailViewPagerAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_detail, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(imageUrlList[position])
    }

    override fun getItemCount(): Int = imageUrlList.size

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(imageUrl: ThemeArtworksResponseItem) {
            with(itemView.findViewById<ImageView>(R.id.gallery_iv)) {
                // URL을 통해 이미지 로드 (Glide 사용)
                Glide.with(context).load(imageUrl.imageUrl).into(this)
                setOnClickListener {
                    onImgClick(imageUrl)
                }
            }
        }
    }
}