package com.hexa.arti.data.model.artmuseum

data class Subscriber(
    val galleryId: Int,
    val profileImageResId: String,
    val name: String? = null,
    val username: String,
    val galleryDescription: String,
    val viewCount: Int
)