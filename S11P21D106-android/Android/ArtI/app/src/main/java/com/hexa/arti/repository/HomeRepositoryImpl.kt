package com.hexa.arti.repository

import android.util.Log
import com.google.gson.Gson
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.home.GetRecommendGalleriesResponse
import com.hexa.arti.data.model.portfolio.PortfolioGenre
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.data.model.response.GetRecommendArtworkResponse
import com.hexa.arti.network.HomeApi
import com.hexa.arti.util.asArtwork
import javax.inject.Inject


class HomeRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi
) : HomeRepository {
    override suspend fun getRecommendGalleries(userId: Int): Result<List<GetRecommendGalleriesResponse>> {
        try {
            val result = homeApi.getRecommendMuseum(userId)
            if (result.isSuccessful) {
                result.body()?.let {
                    return Result.success(it)
                }
                return Result.failure(Exception())
            } else {
                val errorResponse =
                    Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
                return Result.failure(
                    ApiException(
                        code = errorResponse.code,
                        message = errorResponse.message
                    )
                )
            }
        } catch(e: Exception){
            return Result.failure(
                ApiException(
                    code = 0,
                    message = "서버 닫힘"
                )
            )
        }
    }

    override suspend fun getRecommendArtworks(userId: Int): Result<List<Artwork>> {
        try {
            val result = homeApi.getRecommendArtwork(userId)
            if (result.isSuccessful) {
                result.body()?.let {
                    return Result.success(it.map { artworkResponse -> artworkResponse.asArtwork() })
                }
                return Result.failure(Exception())
            } else {
                val errorResponse =
                    Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
                return Result.failure(
                    ApiException(
                        code = errorResponse.code,
                        message = errorResponse.message
                    )
                )
            }
        } catch(e: Exception){
            return Result.failure(
                ApiException(
                    code = 0,
                    message = "서버 닫힘"
                )
            )
        }
    }

    override suspend fun getPortfolio(memberId: Int): Result<List<PortfolioGenre>> {
        try {
            val result = homeApi.getPortfolio(memberId)
            if (result.isSuccessful) {
                result.body()?.let {
                    return Result.success(it)
                }
                return Result.failure(Exception())
            } else {
                val errorResponse =
                    Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
                return Result.failure(
                    ApiException(
                        code = errorResponse.code,
                        message = errorResponse.message
                    )
                )
            }
        } catch(e: Exception){
            return Result.failure(
                ApiException(
                    code = 0,
                    message = "서버 닫힘"
                )
            )
        }
    }
}