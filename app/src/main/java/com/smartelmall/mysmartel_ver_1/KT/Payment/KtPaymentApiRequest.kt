package com.smartelmall.mysmartel_ver_1.KT.Payment

data class KtPaymentApiRequest(
    val header: List<Header>,
    val body: List<Body>
) {
    data class Header(
        val type: String
    )

    data class Body(
        val traceno: String,
        val custId: String,
        val ncn: String,
        val ctn: String,
        val clientIp: String,
        val userId: String,
        val productionDate: String
    )
}
