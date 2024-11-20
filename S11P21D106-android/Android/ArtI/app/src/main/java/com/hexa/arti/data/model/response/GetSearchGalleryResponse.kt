package com.hexa.arti.data.model.response

data class GetSearchGalleryResponse(
    val id: Int,
    val name: String,
    val description: String? = null,
    val image: String,
    val viewCount:Int,
    val ownerId: Int,
)
