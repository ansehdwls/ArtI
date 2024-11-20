package com.hexa.arti.data.model.response

data class GetRandomGalleriesResponse(
    val id: Int,
    val name: String?,
    val description: String?,
    val image: String,
    val viewCount: Int,
    val ownerId: Int,
)