package com.smartel.mysmartel_ver_1

data class KtDeductApiRequest(
    val header: List<Header>,
    val body: List<Body>
) {
    data class Header(val type: String)
    data class Body(
        val traceno: String,
        val custId: String,
        val ncn: String,
        val ctn: String,
        val clientIp: String,
        val userId: String,
        val useMonth: String
    )
}

