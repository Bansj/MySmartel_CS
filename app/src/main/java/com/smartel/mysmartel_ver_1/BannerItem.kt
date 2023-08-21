package com.smartel.mysmartel_ver_1

data class BannerItem(
    val id: Int,
    val imageBucketName: String,
    val imagePath: String,
    val imageFileName: String,
    val imageExtension: String,
    val imageContentType: String,
    val imageLink: String)

data class BannerResponse(
    val items: List<BannerItem>
)