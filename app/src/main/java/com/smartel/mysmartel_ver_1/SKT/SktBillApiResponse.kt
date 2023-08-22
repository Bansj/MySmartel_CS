package com.smartel.mysmartel_ver_1.SKT

data class SktBillApiResponse(val itemName: String, val amount: String, val code: String)
data class ParsedData(
    val 구분코드: String,
    val 업무유형코드: String,
    val 전화번호: String,
    val 서비스계정번호: String,
    val 조회월: String,
    val 총청구금액: String,
    val 청구서건수: Int,
    val 청구서정보: List<CheongGu>
)

data class CheongGu(
    val 청구서대분류명: String,
    val 청구서소분류명: String,
    val 청구서항목명: String,
    val 청구금액: String,
    val 에러코드: String,
    val 종료문자: String
)