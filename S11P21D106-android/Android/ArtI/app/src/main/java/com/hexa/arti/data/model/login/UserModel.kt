package com.hexa.arti.data.model.login

import com.google.gson.annotations.SerializedName

data class UserListModel(
    @SerializedName("data")
    var data: List<UserModel>?
)

data class UserModel (
    @SerializedName("first_name") // 실제 json 데이터의 키 값
    var firstName: String, // 변수에 넣어줌
    @SerializedName("last_name")
    var lastName: String
)