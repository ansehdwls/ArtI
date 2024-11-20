package com.hexa.arti.data.model.response

data class GetRandomGenreArtWorkResponse(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val artist: String,
    val year: String,
)