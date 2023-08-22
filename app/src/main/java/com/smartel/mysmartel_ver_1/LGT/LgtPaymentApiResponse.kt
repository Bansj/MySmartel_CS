package com.smartel.mysmartel_ver_1.LGT

data class LgtPaymentApiResponse(
    val BillInfo: List<LgtBillInfo>,
    val ResultCode: String
)

data class LgtBillInfo(
    val Method: String,
    val PayDate: String,
    val PayAmt: String,
    val PayName: String
)
