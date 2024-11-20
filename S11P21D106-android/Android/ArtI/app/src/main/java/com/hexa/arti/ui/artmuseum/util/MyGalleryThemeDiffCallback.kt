package com.hexa.arti.ui.artmuseum.util

import androidx.recyclerview.widget.DiffUtil
import com.hexa.arti.data.model.artmuseum.MyGalleryThemeItem

object MyGalleryThemeDiffCallback : DiffUtil.ItemCallback<MyGalleryThemeItem>() {
    override fun areItemsTheSame(oldItem: MyGalleryThemeItem, newItem: MyGalleryThemeItem): Boolean {
        return oldItem.id == newItem.id // 제목이 같으면 동일한 아이템
    }

    override fun areContentsTheSame(oldItem: MyGalleryThemeItem, newItem: MyGalleryThemeItem): Boolean {
        return oldItem == newItem // 전체 데이터가 같으면 동일한 콘텐츠
    }

}