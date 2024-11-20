package com.hexa.arti.data.model.artwork

data class Artwork(
    val artworkId: Int,
    val title: String,
    val imageUrl: String,
    val description: String? = null,
    val year: String,
    val artist: String? = null
) {

}