package com.hexa.arti.data.model.response

import com.google.gson.annotations.SerializedName

data class GetRecommendArtworkResponse(
    @SerializedName("artwork_id")
    val artworkId: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val title: String,
    val year: String,
    val writer: String,
)
