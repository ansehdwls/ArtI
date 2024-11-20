package com.hexa.arti.network

import com.hexa.arti.data.model.signup.EmailCodeDto
import com.hexa.arti.data.model.signup.SignUpModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpApi {
    @POST("auth/register")
    suspend fun signUp(@Body signUpModel: SignUpModel) : Response<ResponseBody>

    @POST("auth/request-auth-number")
    suspend fun sendEmail(@Body signUpModel: Map<String,String>) : Response<ResponseBody>

    @POST("auth/verify-code")
    suspend fun checkEmailCode(@Body emailCodeDto: EmailCodeDto) : Response<ResponseBody>
}