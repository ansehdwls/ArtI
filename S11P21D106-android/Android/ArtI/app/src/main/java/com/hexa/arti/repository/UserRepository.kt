package com.hexa.arti.repository

import okhttp3.ResponseBody

interface UserRepository {
    suspend fun postChangeNickname(nickName: String): Result<ResponseBody>

    suspend fun postChangePass(
        currentPassword: String,
        newPassword: String,
        confirmationPassword: String
    ): Result<ResponseBody>

    suspend fun getUserNickname() : Result<String>
}