package com.hexa.arti.network

import com.hexa.arti.data.model.response.GetArtWorkPagingResponse
import com.hexa.arti.data.model.response.GetArtWorkResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtWorkApi {
    @GET("artworks/{artworkId}")
    suspend fun getArtWorkById(@Path("artworkId") artworkId: Int): Response<GetArtWorkResponse>

    @GET("artworks/search")
    suspend fun getArtWorksByString(@Query("keyword") keyword: String): Response<List<GetArtWorkResponse>>

    @GET("artworks/search")
    suspend fun getArtworksByStringWithPaging(
        @Query("page") page: Int,
        @Query("keyword") keyword: String
    ): GetArtWorkPagingResponse
}