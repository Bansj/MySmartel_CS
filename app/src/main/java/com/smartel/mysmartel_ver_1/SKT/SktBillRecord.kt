package com.smartel.mysmartel_ver_1.SKT

data class BillRecord(
    val opClCd: String,
    val opTypCd: String,
    val svcNum: String,
    val svAcntNum: String,
    val invYm: String,
    val totInvAmt: String,
    val billRecCnt: String,
    val billItmLclNm: String,
    val billItmSclNm: String,
    val billItmNm: String,
    val invAmt: String,
    val errorCd: String,
    val endChar: String
)
