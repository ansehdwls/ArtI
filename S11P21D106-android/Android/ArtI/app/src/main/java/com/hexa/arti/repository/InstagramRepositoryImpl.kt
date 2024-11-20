package com.hexa.arti.repository

import android.util.Log
import com.google.gson.Gson
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.network.InstagramApi
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class InstagramRepositoryImpl @Inject constructor(
    private val instagramApi: InstagramApi
) : InstagramRepository {
    override suspend fun postInstagramUrl(url: String): Result<Response<ResponseBody>> {
        try {
            val result = instagramApi.postInstagramToken(mapOf(Pair("url", url)))
            Log.d("확인", "응답확인 ${result}")
            if (result.isSuccessful) {
                result.let {
                    return Result.success(it)
                }
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
        } catch (e: Exception) {
            return Result.failure(
                ApiException(
                    code = 0,
                    message = "서버 닫힘"
                )
            )
        }
    }
}