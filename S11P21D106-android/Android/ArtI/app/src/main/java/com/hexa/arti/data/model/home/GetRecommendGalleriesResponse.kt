package com.hexa.arti.data.model.home

import com.google.gson.annotations.SerializedName

data class GetRecommendGalleriesResponse(
    @SerializedName("gallery")
    val homeGallery: HomeGallery,
    @SerializedName("user")
    val homeUser: HomeUser,
    @SerializedName("theme")
    val homeThemes: List<HomeTheme>
)

data class HomeGallery(
    val id: Int? = null,
    @SerializedName("gallery_id")
    val galleryId: Int,
    @SerializedName("gallery_title")
    val galleryTitle: String,
    @SerializedName("gallery_desc")
    val galleryDescription: String,
    @SerializedName("gallery_img")
    val galleryThumbnail: String,
    @SerializedName("gallery_view")
    val galleryView: Int,
)

data class HomeUser(
    val id: Int? = null,
    @SerializedName("member_id")
    val memberId: Int,
    val email: String,
    val nickname: String,
)

data class HomeTheme(
    val id: Int? = null,
    @SerializedName("theme_id")
    val themeId: Int,
    @SerializedName("theme_name")
    val themeName: String,
    @SerializedName("artworks")
    val artworks: List<PreviewImage>
)

data class PreviewImage(
    val id: Int? = null,
    @SerializedName("artwork_id")
    val artworkId: Int,
    @SerializedName("image_url")
    val imageUrl: String,
)
