package com.hexa.arti.network

import com.hexa.arti.data.model.response.GetArtistResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtistApi {
    @GET("artists/search")
    suspend fun getArtistByString(@Query("keyword") keyword: String): Response<List<GetArtistResponse>>

    @GET("artists/random")
    suspend fun getRandomArtists(): Response<List<GetArtistResponse>>

    @GET("artists/by-genre")
    suspend fun getRepresentArtists(@Query("genre") genre: String): Response<List<GetArtistResponse>>
}