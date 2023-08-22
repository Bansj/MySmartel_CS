package com.smartel.mysmartel_ver_1.KT
data class KtBillApiResponse(
    val header: List<Header>,
    val body: List<Body>
)
data class Header(
    val type: String
)
data class Body(
    val traceno: String,
    val dateView: String,
    val useDate: String,
    val detListDto: List<DetListDto>,
    val hndFarDto: List<Any>,
    val result: String,
    val resultCd: String,
    val resultMsg: String
)
data class DetListDto(
    val splitDescription: String,
    val actvAmt: String,
    val billSeqNo: String,
    val messageLine: String
)




