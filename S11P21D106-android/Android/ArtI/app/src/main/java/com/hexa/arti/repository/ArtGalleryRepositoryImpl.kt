package com.hexa.arti.repository

import android.util.Log
import com.google.gson.Gson
import com.hexa.arti.data.model.artmuseum.ArtGalleryResponse
import com.hexa.arti.data.model.artmuseum.CreateThemeDto
import com.hexa.arti.data.model.artmuseum.GalleryBanner
import com.hexa.arti.data.model.artmuseum.GalleryRequest
import com.hexa.arti.data.model.artmuseum.GalleryResponse
import com.hexa.arti.data.model.artmuseum.GetTotalThemeResponse
import com.hexa.arti.data.model.artmuseum.MyGalleryThemeItem
import com.hexa.arti.data.model.artmuseum.SubscriptionGallery
import com.hexa.arti.data.model.artmuseum.ThemeResponseItem
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.ErrorResponse
import com.hexa.arti.data.model.response.GetSearchGalleryResponse
import com.hexa.arti.network.GalleryApi
import com.hexa.arti.util.asArtwork
import com.hexa.arti.util.asGalleryBanner
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

class ArtGalleryRepositoryImpl @Inject constructor(
    private val galleryAPI: GalleryApi
) : ArtGalleryRepository {

    override suspend fun getThemeWithArtworks(galleryId: Int): Result<List<GetTotalThemeResponse>> {
        val result = galleryAPI.getThemeWithArtworks(galleryId)

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

    override suspend fun getSearchGalleries(keyword: String): Result<List<GetSearchGalleryResponse>> {
        val result = galleryAPI.getSearchGallery(keyword)

        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it)
            }
            return Result.failure(Exception())
        } else {
            return Result.failure(
                ApiException(
                    code = 1000,
                    message = "통신에러"
                )
            )
//            val errorResponse =
//                Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
//            return Result.failure(
//                ApiException(
//                    code = errorResponse.code,
//                    message = errorResponse.message
//                )
//            )
        }
    }

    override suspend fun getRandomGalleries(): Result<List<GalleryBanner>> {
        val result = galleryAPI.getRandomGalleries()

        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it.map { gallery -> gallery.asGalleryBanner() })
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

    override suspend fun getRandomGenreArtworks(genreLabel: String): Result<List<Artwork>> {
        val result = galleryAPI.getRandomGenreArtworks(genreLabel)

        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it.map { getRandomGenreArtWorkResponse -> getRandomGenreArtWorkResponse.asArtwork() })
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

    override suspend fun getArtGallery(galleryId: Int): Result<ArtGalleryResponse> {
        val result = galleryAPI.getGalley(galleryId)

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

    override suspend fun getArtGalleryThemes(galleryId: Int): Result<List<MyGalleryThemeItem>> {
        val result = galleryAPI.getGalleryTheme(galleryId)
        Log.d("확인", "getArtGalleryThemes: $galleryId")
        if (result.isSuccessful) {
            result.body()?.let {
                Log.d("확인", "getArtGalleryThemes: $it")
                val myGalleryThemeItems = it.map { themeItem ->
                    Log.d("확인", "getArtGalleryThemes: $themeItem")
                    // 각 테마 ID에 대해 테마 이미지 가져오기
                    val artworkResult = galleryAPI.getGalleryThemeArtwork(themeItem.id)
                    if (artworkResult.isSuccessful) {
                        val artworkList = artworkResult.body() ?: emptyList()
                        val imageUrls = artworkList.map { it } // 이미지 URL만 추출

                        // MyGalleryThemeItem 생성
                        MyGalleryThemeItem(
                            id = themeItem.id,
                            title = themeItem.name,
                            images = imageUrls
                        )
                    } else {
                        // 실패 시 빈 이미지 리스트로 처리
                        MyGalleryThemeItem(
                            id = themeItem.id,
                            title = themeItem.name,
                            images = emptyList()
                        )
                    }
                }
                return Result.success(myGalleryThemeItems)
            }
            return Result.failure(Exception())
        }
        // 오류 응답 처리
        return Result.failure(
            ApiException(
                code = 9999,
                message = "통신에러"
            )
        )
//        val errorBody = result.errorBody()?.string()
//        val errorResponse = if (errorBody != null) {
//            Gson().fromJson(errorBody, ErrorResponse::class.java)
//        } else {
//            ErrorResponse(code = result.code(), message = "Unknown error")
//        }

    }

    override suspend fun getSubscriptionGalleries(memberId: Int): Result<SubscriptionGallery> {
        val result = galleryAPI.getSubscriptionsGalleries(memberId)

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

    override suspend fun postGallery(
        image: MultipartBody.Part,
        galleryDto: GalleryRequest
    ): Result<GalleryResponse> {

        val userJson = Gson().toJson(galleryDto)
        val userRequestBody = userJson.toRequestBody("application/json".toMediaTypeOrNull())
        val requestBody = MultipartBody.Part.createFormData("galleryRequest", null, userRequestBody)

        val result = galleryAPI.postGallery(requestBody, image)

        if (result.isSuccessful) {
            result.body()?.let {
                return Result.success(it)
            }

            return Result.failure(Exception())
        } // 오류 응답 처리
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


    override suspend fun postTheme(themeDto: CreateThemeDto): Result<ThemeResponseItem> {
        val result = galleryAPI.postGalleryTheme(themeDto)
        Log.d("확인", "postTheme: $themeDto")
        Log.d("확인", "postTheme: $result")
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

    override suspend fun postArtworkTheme(
        themeId: Int,
        artworkId: Int,
        description: String
    ): Result<ResponseBody> {
        val result = galleryAPI.postArtworkTheme(themeId, artworkId, description)

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


    override suspend fun updateArtGallery(
        galleryId: Int,
        image: MultipartBody.Part,
        galleryDto: GalleryRequest
    ): Result<ResponseBody> {
        val userJson = Gson().toJson(galleryDto)
        val userRequestBody = userJson.toRequestBody("application/json".toMediaTypeOrNull())
        val requestBody = MultipartBody.Part.createFormData("galleryRequest", null, userRequestBody)
        Log.d("확인", "updateArtGallery: $image")
        Log.d("확인", "updateArtGallery: $galleryDto")
        val result = galleryAPI.updateMyGallery(galleryId, requestBody, image)

        Log.d("확인", "updateArtGallery: $result")

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

    override suspend fun deleteTheme(galleryId: Int, themeId: Int): Result<ResponseBody> {
        val result = galleryAPI.deleteTheme(galleryId, themeId)
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

    override suspend fun deleteThemeArtWork(themeId: Int, artworkId: Int): Result<ResponseBody> {
        val result = galleryAPI.deleteThemeArtwork(themeId, artworkId)
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

    override suspend fun deleteThemeArtWorkAI(themeId: Int, artworkId: Int): Result<ResponseBody> {
        val result = galleryAPI.deleteThemeArtworkAI(themeId, artworkId)
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