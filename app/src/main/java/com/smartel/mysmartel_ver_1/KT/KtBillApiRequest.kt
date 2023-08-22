package com.smartel.mysmartel_ver_1.KT

data class KtBillApiRequest(
    val header: List<KtBillApiHeader>,
    val body: List<KtBillApiBody>
)

data class KtBillApiHeader(
    val type: String
)

data class KtBillApiBody(
    val traceno: String,
    val custId: String,
    val ncn: String,
    val ctn: String,
    val clientIp: String,
    val userId: String,
    val billSeqNo: String,
    val billDueDateList: String,
    val billMonth: String,
    val billStartDate: String,
    val billEndDate: String
)