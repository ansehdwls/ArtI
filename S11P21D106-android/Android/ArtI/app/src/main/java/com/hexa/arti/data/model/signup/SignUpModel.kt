package com.hexa.arti.data.model.signup

import com.google.gson.annotations.SerializedName

data class SignUpModel(
    val email : String,
    @SerializedName("nickname")
    val nickName: String,
    val password: String
)
