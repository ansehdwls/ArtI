package com.hexa.arti.data.model.response

data class ErrorResponse(
    val code: Int?,
    val message: String?
)


data class ApiException(
    val code : Int?,
    override val message : String?
) : Exception(message)

// 서버가 에러 메시지를 안던져주면 에러 매퍼만들기