package com.hexa.arti.data.model.artmuseum

data class GetTotalThemeResponse(
    val themeId: Int,
    val themeName: String,
    val artworks: List<ThemeArtwork>
)

data class ThemeArtwork(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val artist: String,
    val year: String? = null,
    val isFocus: Boolean = false,
)