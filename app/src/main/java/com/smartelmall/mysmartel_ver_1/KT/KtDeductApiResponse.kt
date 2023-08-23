package com.smartelmall.mysmartel_ver_1.KT

data class KtDeductApiResponse(
    val header: List<HeaderData>,
    val body: List<BodyData>
) {
    data class HeaderData(
        val type: String
    )
    data class BodyData(
        val traceno: String,
        val totaluseTimeDto: List<TotaluseTimeDtoData>,
        val voiceCallDetailDto: List<VoiceCallDetailDtoData>,
        val voiceCallDetailTotDto: List<VoiceCallDetailTotDtoData>,
        val totUseTimeCntDto: List<TotUseTimeCntDtoData>,
        val totUseTimeCntTotDto: List<TotUseTimeCntTotDtoData>,
        val result: String,
        val resultCd: String,
        val resultMsg: String
    ) {
        data class TotaluseTimeDtoData(
            val strBunGun: String,
            val strSvcName: String,
            var strCtnSecs: String,
            val strSecsToRate: String,
            val strSecsToAmt: String,
            var strFreeMinCur: String,
            var strFreeminRoll: String,
            var strFreeMinTotal: String,
            var strFreeMinUse: String,
            var strFreeMinReMain: String
        )

        data class VoiceCallDetailDtoData(
            val strBunGun: String,
            val strSvcName: String,
            val strFreeMinCur: String,
            val strFreeminRoll: String,
            val strFreeMinTotal: String,
            val strFreeMinUse: String,
            val strFreeMinReMain: String
        )

        data class VoiceCallDetailTotDtoData(
            val total: String,
            val iFreeminCurSum: String,
            val iFreeMinRollSum: String,
            val iFreeminTotalSum: String,
            val iFreeMinUseSum: String,
            val iFreeMinRemainSum: String
        )

        data class TotUseTimeCntDtoData(
            val strSvcNameSms: String,
            val strCtnSecs: String,
            val strFreeSmsCur: String,
            val strFreeSmsRoll: String,
            val strFreeSmsTotal: String,
            val strFreesmsUse: String,
            val strFreeSmsRemain: String
        )

        data class TotUseTimeCntTotDtoData(
            val total: String,
            val strFreeSmsCur: String,
            val strFreeSmsRoll: String,
            val strFreeSmsTotal: String,
            val strFreeSmsuse: String,
            val strFreeSmsRemain: String
        )
    }
}







