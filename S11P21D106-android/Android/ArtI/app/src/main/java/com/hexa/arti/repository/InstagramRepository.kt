package com.hexa.arti.repository

import okhttp3.ResponseBody
import retrofit2.Response

interface InstagramRepository {

    suspend fun postInstagramUrl(url: String): Result<Response<ResponseBody>>
}