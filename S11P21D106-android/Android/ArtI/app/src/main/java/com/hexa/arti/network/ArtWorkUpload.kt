package com.hexa.arti.network

import com.hexa.arti.data.model.artworkupload.ArtWorkAIDto
import com.hexa.arti.data.model.artworkupload.ArtWorkUploadDto
import com.hexa.arti.data.model.artworkupload.ArtworkAIResponse
import com.hexa.arti.data.model.survey.SurveyListDto
import com.hexa.arti.data.model.survey.SurveyResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ArtWorkUpload {
    @Multipart
    @POST("artwork/ai")
    suspend fun makeAIImage(
        @Part contentImage: MultipartBody.Part,
        @Part("style_image") styleImage: RequestBody
    ): Response<ArtWorkUploadDto>

    @GET("start/")
    suspend fun getSurveyImage() : Response<SurveyResponse>

    @POST("start/save")
    suspend fun saveSurvey(@Body surveyListDto: SurveyListDto) : Response<ResponseBody>

    @POST("artwork/ai/save")
    suspend fun saveAIImage(@Body artWorkAIDto : ArtWorkAIDto) : Response<ArtworkAIResponse>
}