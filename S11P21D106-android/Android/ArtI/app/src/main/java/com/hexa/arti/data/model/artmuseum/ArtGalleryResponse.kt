package com.hexa.arti.data.model.artmuseum

data class ArtGalleryResponse(
    val description: String,
    val id: Int,
    val image: String,
    val name: String,
    val ownerId: Int,
    val viewCount: Int
)