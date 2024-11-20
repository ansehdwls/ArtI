package com.hexa.arti.data.model.artmuseum

data class UpdateGalleryDto(
    val description: String,
    val image: String,
    val name: String,
    val ownerId: Int
)