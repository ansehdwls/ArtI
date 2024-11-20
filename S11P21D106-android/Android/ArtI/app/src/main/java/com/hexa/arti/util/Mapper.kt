package com.hexa.arti.util

import com.hexa.arti.data.model.artmuseum.GalleryBanner
import com.hexa.arti.data.model.artmuseum.GetTotalThemeResponse
import com.hexa.arti.data.model.artmuseum.ThemeArtwork
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.home.HomeTheme
import com.hexa.arti.data.model.home.PreviewImage
import com.hexa.arti.data.model.response.GetArtWorkResponse
import com.hexa.arti.data.model.response.GetArtistResponse
import com.hexa.arti.data.model.response.GetRandomGalleriesResponse
import com.hexa.arti.data.model.response.GetRandomGenreArtWorkResponse
import com.hexa.arti.data.model.response.GetRecommendArtworkResponse
import com.hexa.arti.data.model.response.GetSearchGalleryResponse
import com.hexa.arti.data.model.search.Artist

fun GetArtistResponse.asArtist() = Artist(
    artistId = this.artistId,
    korName = this.korName,
    engName = this.engName,
    imageUrl = this.image,
    description = this.summary
)

fun GetArtWorkResponse.asArtwork() = Artwork(
    artworkId = this.artworkId,
    imageUrl = this.filename,
    title = this.title,
    description = this.description,
    year = this.year,
    artist = this.artist
)

fun GetSearchGalleryResponse.asGalleryBanner() = GalleryBanner(
    galleryId = this.id,
    name = this.name,
    description = this.description,
    imageUrl = this.image,
    viewCount = this.viewCount,
    ownerId = this.ownerId
)

fun GetRandomGenreArtWorkResponse.asArtwork() = Artwork(
    artworkId = this.id,
    imageUrl = this.imageUrl,
    title = this.title,
    artist = this.artist,
    description = this.description,
    year = this.year
)

fun GetRecommendArtworkResponse.asArtwork() = Artwork(
    artworkId = this.artworkId,
    imageUrl = this.imageUrl,
    title = this.title,
    year = this.year,
    artist = this.writer,
)

fun GetRandomGalleriesResponse.asGalleryBanner() = GalleryBanner(
    galleryId = this.id,
    imageUrl = this.image,
    ownerId = this.ownerId,
    name = this.name,
    description = this.description,
    viewCount = this.viewCount
)

fun ThemeArtwork.asPreviewImage() = PreviewImage(
    artworkId = this.id,
    imageUrl = this.imageUrl
)

fun GetTotalThemeResponse.asHomeTheme() = HomeTheme(
    themeId = this.themeId,
    themeName = this.themeName,
    artworks = this.artworks.map { it.asPreviewImage() }
)