package com.hexa.arti.config

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass : Application() {

    companion object {
        val REPRESENT_ARTWORKS = listOf(
            "Abstract_Expressionism",
            "Expressionism",
            "Color_Field_Painting",
            "Synthetic_Cubism",
            "Minimalism",
            "Pop_art",
            "Naive_Art_Primitivism",
            "Cubism",
            "Impressionism",
            "Post_Impressionism",
            "Realism",
            "Fauvism",
            "Action_painting",
            "Pointillism",
            "Contemporary_Realism",
            "Symbolism",
            "Analytical_Cubism",
            "Art_Nouveau_Modern",
            "Romanticism",
            "New_Realism",
            "Early_Renaissance",
            "Baroque",
            "Rococo",
            "Northern_Renaissance",
            "High_Renaissance",
            "Ukiyo_e",
            "Mannerism_Late_Renaissance"
        )
        val REPRESENT_ARTWORKS_KOR = listOf(
            "추상표현주의",
            "표현주의",
            "컬러 필드",
            "종합적 입체파",
            "미니멀리즘",
            "팝 아트",
            "나이브 아트",
            "입체주의",
            "인상주의",
            "탈인상주의",
            "사실주의",
            "야수파",
            "액션페인팅",
            "점묘법",
            "현대적 사실주의",
            "상징주의",
            "분석적 입체파",
            "아르누보",
            "낭만주의",
            "새로운 현실주의",
            "르네상스",
            "바로크",
            "로코코",
            "북방 르네상스",
            "전성기 르네상스",
            "우키요에",
            "매너리즘"
        )

        val KOREAN_TO_ENGLISH_MAP: Map<String, String> by lazy {
            REPRESENT_ARTWORKS_KOR.zip(REPRESENT_ARTWORKS).toMap()
        }

        val ENGLISH_TO_KOREAN_MAP: Map<String, String> by lazy {
            REPRESENT_ARTWORKS.zip(REPRESENT_ARTWORKS_KOR).toMap()
        }
    }

}