package com.hexa.arti.repository

import com.hexa.arti.data.model.artmuseum.ArtGalleryResponse
import com.hexa.arti.data.model.artmuseum.CreateThemeDto
import com.hexa.arti.data.model.artmuseum.GalleryRequest
import com.hexa.arti.data.model.artmuseum.GalleryResponse
import com.hexa.arti.data.model.artmuseum.GalleryBanner
import com.hexa.arti.data.model.artmuseum.GetTotalThemeResponse
import com.hexa.arti.data.model.artmuseum.MyGalleryThemeItem
import com.hexa.arti.data.model.artmuseum.SubscriptionGallery
import com.hexa.arti.data.model.artmuseum.ThemeResponseItem
import com.hexa.arti.data.model.artmuseum.UpdateGalleryDto
import okhttp3.MultipartBody
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.response.GetSearchGalleryResponse
import okhttp3.ResponseBody

interface ArtGalleryRepository {

    suspend fun getThemeWithArtworks(galleryId: Int): Result<List<GetTotalThemeResponse>>

    suspend fun getSearchGalleries(keyword: String): Result<List<GetSearchGalleryResponse>>

    suspend fun getRandomGalleries(): Result<List<GalleryBanner>>

    suspend fun getRandomGenreArtworks(
        genreLabel: String,
    ): Result<List<Artwork>>

    suspend fun getArtGallery(
        galleryId: Int
    ): Result<ArtGalleryResponse>

    suspend fun getArtGalleryThemes(
        galleryId: Int
    ): Result<List<MyGalleryThemeItem>>

    suspend fun getSubscriptionGalleries(
        memberId : Int
    ): Result<SubscriptionGallery>

    suspend fun postGallery(
        image : MultipartBody.Part,
        galleryDto: GalleryRequest
    ) : Result<GalleryResponse>

    suspend fun postTheme(
        themeDto: CreateThemeDto
    ): Result<ThemeResponseItem>

    suspend fun postArtworkTheme(
        themeId: Int,
        artworkId: Int,
        description: String
    ): Result<ResponseBody>

    suspend fun updateArtGallery(
        galleryId : Int,
        image : MultipartBody.Part,
        galleryDto: GalleryRequest
    ) : Result<ResponseBody>

    suspend fun deleteTheme(
        galleryId: Int,
        themeId: Int
    ): Result<ResponseBody>


    suspend fun deleteThemeArtWork(
        themeId: Int,
        artworkId: Int
    ): Result<ResponseBody>


    suspend fun deleteThemeArtWorkAI(
        themeId: Int,
        artworkId: Int
    ): Result<ResponseBody>

}