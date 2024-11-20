package com.hexa.arti.data.model.profile

data class ChangePass(
    val confirmationPassword: String,
    val currentPassword: String,
    val newPassword: String
)