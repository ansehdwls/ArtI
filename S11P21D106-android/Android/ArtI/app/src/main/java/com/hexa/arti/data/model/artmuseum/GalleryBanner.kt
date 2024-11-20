package com.hexa.arti.data.model.artmuseum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryBanner(
    val galleryId: Int,
    val name: String?,
    val description: String?,
    val imageUrl: String,
    val viewCount: Int,
    val ownerId: Int,
) : Parcelable
