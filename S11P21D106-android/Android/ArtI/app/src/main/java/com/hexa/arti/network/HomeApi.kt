package com.hexa.arti.network

import com.hexa.arti.data.model.home.GetRecommendGalleriesResponse
import com.hexa.arti.data.model.portfolio.PortfolioGenre
import com.hexa.arti.data.model.response.GetRecommendArtworkResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {

    @GET("home/{user_id}")
    suspend fun getRecommendMuseum(@Path("user_id") userId: Int): Response<List<GetRecommendGalleriesResponse>>

    @GET("home/artwork/{user_id}")
    suspend fun getRecommendArtwork(@Path("user_id") userId: Int): Response<List<GetRecommendArtworkResponse>>

    @GET("portfolio/{member_id}")
    suspend fun getPortfolio(@Path("member_id") memberId: Int): Response<List<PortfolioGenre>>
}