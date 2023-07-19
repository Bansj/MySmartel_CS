package com.smartel.mysmartel_ver_1

import com.google.gson.annotations.SerializedName

data class LgtBillApiResponse(
    @SerializedName("BillInfo")
    val billInfo: List<BillInfo>,
    @SerializedName("ResultCode")
    val ResultCode: String
)

data class BillInfo(
    @SerializedName("svcNm")
    val svcNm: String,
    @SerializedName("blItemNm")
    val blItemNm: String,
    @SerializedName("billAmt")
    val billAmt: String,
    @SerializedName("vatPrntYn")
    val vatPrntYn: String,
    @SerializedName("leafNodeYn")
    val leafNodeYn: String,
    @SerializedName("totCnt")
    val totCnt: String
)

