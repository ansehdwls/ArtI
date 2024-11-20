package com.hexa.arti.repository

import com.hexa.arti.data.model.login.LoginResponse

interface LoginRepository {
    suspend fun postLogin(email : String, password: String) : Result<LoginResponse>
}