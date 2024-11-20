package com.hexa.arti.network

import okhttp3.Interceptor
import okhttp3.Response

//class ErrorInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val response = chain.proceed(request)
//        if (response.isSuccessful.not() && response.code == 404) {
//           throw APiExcpetion()
//            return
//        }
//        return response
//    }
//}
//
//data class ErrorResponse(
//    val errorCode: Int,
//    val errorMessage: String,
//)