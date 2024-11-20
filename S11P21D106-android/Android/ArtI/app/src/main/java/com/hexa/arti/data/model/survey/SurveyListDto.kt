package com.hexa.arti.data.model.survey

import com.google.gson.annotations.SerializedName

data class SurveyListDto(
    val lst: String,
    @SerializedName("member_id")
    val memberId: Int
)