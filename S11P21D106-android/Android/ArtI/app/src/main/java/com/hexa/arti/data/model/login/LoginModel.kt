package com.hexa.arti.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("email")
    val email : String,
    val password : String
)
