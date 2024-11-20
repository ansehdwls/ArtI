package com.hexa.arti.network

import com.hexa.arti.data.model.music.MusicDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MusicApi {

    @GET("music/{gallery_id}")
    suspend fun getMusic(@Path("gallery_id") galleryId : Int) : Response<ResponseBody>

    @POST("music/{gallery_id}")
    suspend fun postMusic(@Path("gallery_id") galleryId : Int) : Response<MusicDto>
}