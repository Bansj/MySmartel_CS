package com.smartelmall.mysmartel_ver_1

data class BannerResponse(
    val id: Int,
    val imageBucketName: String,
    val imagePath: String,
    val imageFileName: String,
    val imageExtension: String,
    val imageContentType: String,
    val imageLink: String)

