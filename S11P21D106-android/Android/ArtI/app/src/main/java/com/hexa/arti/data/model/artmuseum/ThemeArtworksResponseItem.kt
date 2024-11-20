package com.hexa.arti.data.model.artmuseum

data class ThemeArtworksResponseItem(
    val description: String,
    val id: Int,
    val imageUrl: String,
    val title: String,
    val artist : String,
    val year : String
)