package com.smartelmall.mysmartel_ver_1.KT

data class KtPaymentApiResponse(
    val header: List<Header>,
    val body: List<Body>
) {
    data class Header(
        val type: String
    )

    data class Body(
        val traceno: String,
        val ban: String,
        val billEndDateList: String,
        val billSeqNumList: String,
        val billStartDateList: String,
        val ctnNumTotalSum: String,
        val ctnNumproductionDate: String,
        val payMentDto: List<PaymentDto>,
        val payMentSumDto: List<Any>,
        val result: String,
        val resultCd: String,
        val resultMsg: String
    ) {
        data class PaymentDto(
            val billDueDateList: String,
            val billMonth: String,
            val billEndDate: String,
            val billSeqNo: String,
            val billStartDate: String,
            val pastDueAmt: String,
            val thisMonth: String,
            val totalDueAmt: String,
            val rmnyAmt: String
        )
    }
}
