package com.smartel.mysmartel_ver_1

data class LgtDedcutApiResponse(
    val remainInfo: List<RemainInfo>,
    val ResultCode: String
)

data class RemainInfo(
    val svcNm: String,
    val svcTypNm: String,
    val svcUnitCd: String,
    val alloValue: String,
    val useValue: String,
    val prodTypeCd: String
)
