package com.hexa.arti.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.data.model.signup.EmailCodeDto
import com.hexa.arti.data.model.signup.SignUpModel
import com.hexa.arti.network.SignUpApi
import okhttp3.ResponseBody
import javax.inject.Inject

private const val TAG = "SignUpRepositoryImpl"
class SignUpRepositoryImpl @Inject constructor(
   private val signUpApi: SignUpApi
): SignUpRepository {

    override suspend fun postSignUp(signUpModel: SignUpModel) : Result<ResponseBody> {
        val result = signUpApi.signUp(signUpModel)
        Log.d(TAG, "postSignUp: ${result}")
        // 성공적인 응답 처리
        if (result.isSuccessful) {
            Log.d(TAG, "postSignUp: ${result.body()}")
            return result.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Response body is null"))
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

    override suspend fun sendEmail(email: String) : Result<ResponseBody> {
        val result = signUpApi.sendEmail(mapOf(Pair("email",email)))
        Log.d(TAG, "postSignUp: ${result}")
        // 성공적인 응답 처리
        if (result.isSuccessful) {
            Log.d(TAG, "postSignUp: ${result.body()}")
            return result.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Response body is null"))
        }

        // 오류 응답 처리
        val errorBody = result.errorBody()?.string()
        Log.d(TAG, "Error body: $errorBody")

        // 오류 응답이 객체가 아닌 단순 문자열일 수 있으므로 이를 처리
        return if (errorBody != null) {
            try {
                // 객체 형식의 오류일 경우
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Result.failure(
                    ApiException(
                        code = errorResponse.code,
                        message = errorResponse.message
                    )
                )
            } catch (e: JsonSyntaxException) {
                // 오류 응답이 문자열일 경우
                Result.failure(
                    ApiException(
                        code = result.code(),
                        message = errorBody
                    )
                )
            }
        } else {
            Result.failure(
                ApiException(
                    code = result.code(),
                    message = "Unknown error"
                )
            )
        }
    }

    override suspend fun checkEmailCode(email: String, code: String): Result<ResponseBody> {

        val result = signUpApi.checkEmailCode(EmailCodeDto(code,email))
        Log.d(TAG, "postSignUp: $result")

        // 성공적인 응답 처리
        if (result.isSuccessful) {
            return result.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Response body is null"))
        }

        // 오류 응답 처리
        val errorBody = result.errorBody()?.string()

        // 응답이 JSON 객체인지 확인
        val errorResponse = try {
            if (errorBody != null && errorBody.startsWith("{")) {
                // JSON 객체로 파싱
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } else {
                // JSON 형식이 아닐 경우 단순 문자열 처리
                ErrorResponse(code = result.code(), message = errorBody ?: "Unknown error")
            }
        } catch (e: JsonSyntaxException) {
            // 파싱 오류 처리
            ErrorResponse(code = result.code(), message = "Invalid error response format")
        }

        return Result.failure(
            ApiException(
                code = errorResponse.code,
                message = errorResponse.message
            )
        )
    }
}