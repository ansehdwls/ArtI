package com.hexa.arti.data.model.response

import com.google.gson.annotations.SerializedName

data class GetArtistResponse(
    @SerializedName("artist_id")
    val artistId: Int,
    val engName: String,
    val korName: String,
    val image: String? = null,
    val summary: String,
)