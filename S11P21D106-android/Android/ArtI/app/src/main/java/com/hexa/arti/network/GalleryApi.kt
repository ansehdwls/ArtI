package com.hexa.arti.network

import com.hexa.arti.data.model.artmuseum.ArtGalleryResponse
import com.hexa.arti.data.model.artmuseum.CreateThemeDto
import com.hexa.arti.data.model.artmuseum.GalleryRequest
import com.hexa.arti.data.model.artmuseum.GalleryResponse
import com.hexa.arti.data.model.artmuseum.GetTotalThemeResponse
import com.hexa.arti.data.model.artmuseum.SubscriptionGallery
import com.hexa.arti.data.model.artmuseum.ThemeArtworksResponse
import com.hexa.arti.data.model.artmuseum.ThemeResponse
import com.hexa.arti.data.model.artmuseum.ThemeResponseItem
import com.hexa.arti.data.model.artmuseum.UpdateGalleryDto
import okhttp3.MultipartBody
import com.hexa.arti.data.model.response.GetRandomGalleriesResponse
import com.hexa.arti.data.model.response.GetRandomGenreArtWorkResponse
import com.hexa.arti.data.model.response.GetSearchGalleryResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface GalleryApi {

    @GET("galleries/{galleryId}/themes-with-artworks")
    suspend fun getThemeWithArtworks(@Path("galleryId") galleryId: Int): Response<List<GetTotalThemeResponse>>

    @GET("galleries/search")
    suspend fun getSearchGallery(@Query("keyword") keyword: String): Response<List<GetSearchGalleryResponse>>

    @GET("galleries/random")
    suspend fun getRandomGalleries(): Response<List<GetRandomGalleriesResponse>>

    @GET("galleries/artworks/random")
    suspend fun getRandomGenreArtworks(@Query("genreLabel") genreLabel: String): Response<List<GetRandomGenreArtWorkResponse>>

    @GET("galleries/{galleryId}")
    suspend fun getGalley(@Path("galleryId") galleryId: Int): Response<ArtGalleryResponse>

    @GET("galleries/{galleryId}/themes")
    suspend fun getGalleryTheme(@Path("galleryId") galleryId: Int): Response<ThemeResponse>

    @GET("galleries/{themeId}/artworks")
    suspend fun getGalleryThemeArtwork(@Path("themeId") themeId: Int): Response<ThemeArtworksResponse>

    // 사용자가 구독한 미술관
    @GET("galleries/subscriptions/{memberId}")
    suspend fun getSubscriptionsGalleries(@Path("memberId") memberId : Int) : Response<SubscriptionGallery>

    @Multipart
    @POST("galleries")
    suspend fun postGallery(@Part galleryRequest: MultipartBody.Part, // JSON 형식의 DTO
                            @Part image: MultipartBody.Part ) : Response<GalleryResponse>

    @POST("galleries/themes")
    suspend fun postGalleryTheme(@Body themeDto: CreateThemeDto) : Response<ThemeResponseItem>

    @POST("galleries/themes/{themeId}/artworks/{artworkId}")
    suspend fun postArtworkTheme(@Path("themeId") themeId: Int, @Path("artworkId") artworkId: Int, @Query("description") description: String) : Response<ResponseBody>

    @POST("galleries/themes/{themeId}/aiartworks/{artworkId}")
    suspend fun postArtworkAITheme(@Path("themeId") themeId: Int, @Path("artworkId") artworkId: Int, @Query("description") description: String) : Response<ResponseBody>

    @Multipart
    @PUT("galleries/{galleryId}")
    suspend fun updateMyGallery(@Path("galleryId") galleryId: Int, @Part galleryRequest: MultipartBody.Part,
                                @Part image: MultipartBody.Part) : Response<ResponseBody>

    @PUT("galleries/themes/{themeId")
    suspend fun updateMyGalleryTheme(@Path("themeId") themeId: Int): Response<ResponseBody>


    @DELETE("galleries/{galleryId}/themes/{themeId}")
    suspend fun deleteTheme(@Path("galleryId") galleryId: Int, @Path("themeId") themeId: Int) : Response<ResponseBody>

    @DELETE("/galleries/themes/{themeId}/artworks/{artworkId}")
    suspend fun deleteThemeArtwork(
        @Path("themeId") themeId: Int,
        @Path("artworkId") artworkId: Int
    ): Response<ResponseBody>

    @DELETE("/galleries/themes/{themeId}/aiartworks/{artworkId}")
    suspend fun deleteThemeArtworkAI(
        @Path("themeId") themeId: Int,
        @Path("artworkId") artworkId: Int
    ): Response<ResponseBody>
}