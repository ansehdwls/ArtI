package com.hexa.arti.repository

import android.util.Log
import com.google.gson.Gson
import com.hexa.arti.data.model.profile.ChangePass
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.network.UserAPI
import okhttp3.ResponseBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userAPI: UserAPI
) : UserRepository{
    override suspend fun postChangeNickname(nickName: String): Result<ResponseBody> {
        val result = userAPI.changeNickname(mapOf(Pair("newNickname",nickName)))

        Log.d("확인", "postChangePass: $result")
        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it)
            }
            return Result.failure(Exception())
        }
        // 오류 응답 처리
        val errorBody = result.errorBody()?.string()
        val errorResponse = if (errorBody != null) {
            Gson().fromJson(errorBody, ErrorResponse::class.java)
        } else {
            ErrorResponse(code = result.code(), message = "Unknown error")
        }

        return Result.failure(
            ApiException(
                code = errorResponse.code,
                message = errorResponse.message
            )
        )

    }

    override suspend fun postChangePass(
        currentPassword: String,
        newPassword: String,
        confirmationPassword: String
    ): Result<ResponseBody> {
        val result = userAPI.changePass(
            ChangePass(
            currentPassword = currentPassword, newPassword = newPassword, confirmationPassword = confirmationPassword
        )
        )
        Log.d("확인", "postChangePass: $result")
        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it)
            }
            return Result.failure(Exception())
        }
        // 오류 응답 처리
        val errorBody = result.errorBody()?.string()
        val errorResponse = if (errorBody != null) {
            Gson().fromJson(errorBody, ErrorResponse::class.java)
        } else {
            ErrorResponse(code = result.code(), message = "Unknown error")
        }

        return Result.failure(
            ApiException(
                code = errorResponse.code,
                message = errorResponse.message
            )
        )
    }

    override suspend fun getUserNickname(): Result<String> {
        val result = userAPI.getNickname()
        Log.d("확인", "postChangePass: $result")
        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it)
            }
            return Result.failure(Exception())
        }
        // 오류 응답 처리
        val errorBody = result.errorBody()?.string()
        val errorResponse = if (errorBody != null) {
            Gson().fromJson(errorBody, ErrorResponse::class.java)
        } else {
            ErrorResponse(code = result.code(), message = "Unknown error")
        }

        return Result.failure(
            ApiException(
                code = errorResponse.code,
                message = errorResponse.message
            )
        )
    }
}