package com.hexa.arti.network

import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.hexa.arti.data.model.profile.ChangePass
import com.hexa.arti.data.model.response.PostSubscribeResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MemberApi {

    @POST("members/{memberId}/subscribe/{galleryId}")
    suspend fun postSubscribe(
        @Path("memberId") memberId: Int,
        @Path("galleryId") galleryId: Int,
    ): Response<PostSubscribeResponse>

    @POST("members/{memberId}/unsubscribe/{galleryId}")
    suspend fun postUnsubscribe(
        @Path("memberId") memberId: Int,
        @Path("galleryId") galleryId: Int,
    ): Response<PostSubscribeResponse>
}