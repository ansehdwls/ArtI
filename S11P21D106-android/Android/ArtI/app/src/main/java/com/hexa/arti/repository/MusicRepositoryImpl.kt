package com.hexa.arti.repository

import android.util.Log
import com.google.gson.Gson
import com.hexa.arti.data.model.music.MusicDto
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.network.MusicApi
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicApi: MusicApi
) : MusicRepository {

    override suspend fun postMusic(galleryId: Int): Result<MusicDto> {
        return try {
            val result = musicApi.postMusic(galleryId)
            Log.d("확인", "postMusic: $result")

            if (result.isSuccessful) {
                result.body()?.let { musicDto ->
                    return Result.success(musicDto)
                }
                Log.e("확인", "응답 본문이 null입니다.")
                return Result.failure(Exception("응답 본문이 null입니다."))
            } else {
                val errorBody = result.errorBody()?.string()
                Log.e("확인", "postMusic 실패: $errorBody")

                try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    return Result.failure(
                        ApiException(
                            code = errorResponse.code,
                            message = errorResponse.message
                        )
                    )
                } catch (e: Exception) {
                    Log.e("확인", "ErrorResponse 파싱 오류: ${e.message}")
                    return Result.failure(Exception("서버 오류: $errorBody"))
                }
            }
        } catch (e: Exception) {
            Log.e("확인", "네트워크 또는 JSON 파싱 오류: ${e.message}")
            return Result.failure(e)
        }
    }


    override suspend fun getMusic(galleryId: Int): Result<Response<ResponseBody>> {
        val result = musicApi.getMusic(galleryId)
        Log.d("확인", "getMusic: $result")

        return if (result.isSuccessful) {
            result.body()?.let {
                Log.d("확인", "getMusic 성공: ${it.string()}")
                Result.success(result)
            } ?: run {
                Log.e("확인", "getMusic 실패: 빈 응답")
                Result.failure(Exception("빈 응답"))
            }
        } else {
            val errorBody = result.errorBody()?.string()  // 오류 응답을 문자열로 변환
            Log.e("확인", "getMusic 실패: $errorBody")

            try {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Result.failure(
                    ApiException(
                        code = errorResponse.code,
                        message = errorResponse.message
                    )
                )
            } catch (e: Exception) {
                Result.failure(Exception("서버 오류: $errorBody"))
            }
        }
    }

}