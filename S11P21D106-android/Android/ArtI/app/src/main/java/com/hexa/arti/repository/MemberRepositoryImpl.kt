package com.hexa.arti.repository

import android.util.Log
import com.google.gson.Gson
import com.hexa.arti.data.model.profile.ChangePass
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.data.model.response.PostSubscribeResponse
import com.hexa.arti.network.MemberApi
import okhttp3.ResponseBody
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberApi: MemberApi
) : MemberRepository {


    override suspend fun postSubscribe(memberId: Int, galleryId: Int): Result<PostSubscribeResponse> {
        val result = memberApi.postSubscribe(memberId, galleryId)
        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it)
            }
            return Result.failure(Exception())
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
            return Result.failure(
                ApiException(
                    code = errorResponse.code,
                    message = errorResponse.message
                )
            )
        }
    }

    override suspend fun postUnsubscribe(memberId: Int, galleryId: Int): Result<PostSubscribeResponse> {
        val result = memberApi.postUnsubscribe(memberId, galleryId)

        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it)
            }
            return Result.failure(Exception())
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
            return Result.failure(
                ApiException(
                    code = errorResponse.code,
                    message = errorResponse.message
                )
            )
        }
    }


}