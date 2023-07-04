package com.smartel.mysmartel_ver_1

data class KtDeductApiResponse(
    val header: List<Header>,
    val body: List<Body>
) {
    data class Header(
        val type: String
    )

    data class Body(
        val result: String,
        val resultCd: String,
        val resultMsg: String
    )
}