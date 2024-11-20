package com.hexa.arti.repository

import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.home.GetRecommendGalleriesResponse
import com.hexa.arti.data.model.portfolio.PortfolioGenre

interface HomeRepository {

    suspend fun getRecommendGalleries(userId: Int): Result<List<GetRecommendGalleriesResponse>>

    suspend fun getRecommendArtworks(userId: Int): Result<List<Artwork>>

    suspend fun getPortfolio(memberId: Int): Result<List<PortfolioGenre>>
}