package com.hexa.arti.network

import com.hexa.arti.data.model.login.LoginModel
import com.hexa.arti.data.model.login.LoginResponse
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("auth/authenticate")
    suspend fun postLogin(@Body loginModel : LoginModel) : Response<LoginResponse>
}