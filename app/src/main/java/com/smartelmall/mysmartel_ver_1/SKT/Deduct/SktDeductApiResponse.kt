package com.smartelmall.mysmartel_ver_1.SKT.Deduct

data class SktDeductApiResponse(
    val svcAcntNum: String,
    val RcClCd: String,
    val dedtRecCnt: String,
    val remainInfo: List<SktRemainInfo> = emptyList()
)

data class SktRemainInfo(
    val planId: String,
    val planNm: String,
    val skipCode: String,
    val freePlanName: String,
    val totalQty: String,
    val useQty: String,
    val remQty: String,
    val unitCd: String
)

