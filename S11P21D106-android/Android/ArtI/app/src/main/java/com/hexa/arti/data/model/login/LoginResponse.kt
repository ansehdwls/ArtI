package com.hexa.arti.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("access_token")
    val token: String,
    @SerializedName("memberId")
    val memberId: Int,
    @SerializedName("galleryId")
    val galleryId : Int
)