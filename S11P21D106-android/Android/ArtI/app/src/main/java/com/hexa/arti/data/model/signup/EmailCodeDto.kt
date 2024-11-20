package com.hexa.arti.data.model.signup

import com.google.gson.annotations.SerializedName

data class EmailCodeDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("email")
    val email: String
)