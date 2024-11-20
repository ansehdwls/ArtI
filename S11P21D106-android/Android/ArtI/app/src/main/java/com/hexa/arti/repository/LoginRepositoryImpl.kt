package com.hexa.arti.repository

import com.google.gson.Gson
import com.hexa.arti.data.model.login.LoginModel
import com.hexa.arti.data.model.login.LoginResponse
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.network.LoginApi
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi
) : LoginRepository {

    override suspend fun postLogin(email: String, password: String): Result<LoginResponse> {
        val result = loginApi.postLogin(LoginModel(email,password))

        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it)
            }
            return Result.failure(Exception())
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
            // return Result.failure(exception = Exception())
            return Result.failure(
                ApiException(
                    code = errorResponse.code,
                    message = errorResponse.message
                )
            )
        }
    }
}