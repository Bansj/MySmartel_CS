package com.smartel.mysmartel_ver_1

data class KtDeductApiResponse(
    val header: List<Header>,
    val body: List<Body>,
    val error: String? = null // New error property to store error message
) {
    data class Header(
        val type: String
    )

    data class Body(
        val traceno: String,
        val result: String,
        val resultCd: String,
        val resultMsg: String
    )
}