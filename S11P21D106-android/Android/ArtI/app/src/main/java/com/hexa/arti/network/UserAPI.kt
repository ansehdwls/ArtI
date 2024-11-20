package com.hexa.arti.network

import com.hexa.arti.data.model.profile.ChangePass
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserAPI {

    @POST("members/change-nickname")
    suspend fun changeNickname(
        @Body nickname: Map<String,String>
    ) : Response<ResponseBody>

    @POST("members/change-password")
    suspend fun changePass(
        @Body password: ChangePass
    ) : Response<ResponseBody>

    @GET("members/nickname")
    suspend fun getNickname() : Response<String>
}