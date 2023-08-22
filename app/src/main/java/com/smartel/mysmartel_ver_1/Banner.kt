package com.smartel.mysmartel_ver_1

data class Banner(
    val id: Int,
    val imageBucketName: String,
    val imagePath: String,
    val imageFileName: String,
    val imageExtension: String,
    val imageContentType: String,
    val imageLink: String
)