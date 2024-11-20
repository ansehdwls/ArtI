package com.hexa.arti.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InstagramApi {

    @POST("instagram/save-token")
    suspend fun postInstagramToken(@Body url: Map<String, String>): Response<ResponseBody>
}