package com.hexa.arti.repository

import com.hexa.arti.data.model.music.MusicDto
import okhttp3.ResponseBody
import retrofit2.Response

interface MusicRepository {
    suspend fun postMusic(
        galleryId : Int
    ) : Result<MusicDto>

    suspend fun getMusic(
        galleryId : Int
    ) :  Result<Response<ResponseBody>>

}