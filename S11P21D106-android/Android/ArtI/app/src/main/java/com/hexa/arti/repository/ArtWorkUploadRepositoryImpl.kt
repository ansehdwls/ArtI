package com.hexa.arti.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.hexa.arti.data.model.artworkupload.ArtWorkAIDto
import com.hexa.arti.data.model.artworkupload.ArtWorkAIUploadDto
import com.hexa.arti.data.model.artworkupload.ArtWorkUploadDto
import com.hexa.arti.data.model.artworkupload.ArtworkAIResponse
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.data.model.survey.SurveyListDto
import com.hexa.arti.data.model.survey.SurveyResponse
import com.hexa.arti.network.ArtWorkUpload
import com.hexa.arti.network.GalleryApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import javax.inject.Inject

private const val TAG = "확인"

class ArtWorkUploadRepositoryImpl @Inject constructor(
    private val artWorkUpload: ArtWorkUpload,
    private val galleryApi: GalleryApi
) : ArtWorkUploadRepository {
    override suspend fun postMakeAIImage(
        contentImage: MultipartBody.Part,
        styleImage: RequestBody
    ): Result<ArtWorkUploadDto> {
        val result = artWorkUpload.makeAIImage(contentImage, styleImage)

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

    override suspend fun getSurveyImage(): Result<SurveyResponse> {
        val result = artWorkUpload.getSurveyImage()

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

    override suspend fun saveSurvey(surveyListDto: SurveyListDto): Result<ResponseBody> {
        val result = artWorkUpload.saveSurvey(surveyListDto)

        if (result.isSuccessful) {
            Log.d(TAG, "saveSurvey: ${result.body()}")
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

    override suspend fun saveAIImage(artWorkAIDto: ArtWorkAIDto): Result<ArtworkAIResponse> {
        Log.d(TAG, "saveSurvey1111111111111: ${artWorkAIDto}")
        val result = artWorkUpload.saveAIImage(artWorkAIDto)
        Log.d(TAG, "saveSurvey: ${result}")
        if (result.isSuccessful) {
            Log.d(TAG, "saveSurvey: ${result.body()}")
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

    override suspend fun saveArtwork(
        themeId: Int,
        artworkId: Int,
        description: String
    ): Result<ResponseBody> {
        Log.d(TAG, "saveArtwork: $themeId $artworkId $description")
        val result = galleryApi.postArtworkTheme(themeId, artworkId, description)
        Log.d("확인", "saveArtwork: $result")
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

    override suspend fun saveArtworkAI(
        themeId: Int,
        artworkId: Int,
        description: String
    ): Result<ResponseBody> {

        Log.d(TAG, "saveArtwork: $themeId $artworkId $description")
        val result = galleryApi.postArtworkAITheme(themeId, artworkId, description)
        Log.d("확인", "saveArtwork: $result")
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