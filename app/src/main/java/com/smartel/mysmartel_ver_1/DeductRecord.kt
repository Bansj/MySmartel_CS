package com.smartel.mysmartel_ver_1


// SKT공제량
data class DeductRecord(
    val planId: String,
    val planName: String,
    val skipCode: String,
    val freePlanName: String,
    val totalQty: String,
    val useQty: String,
    val remQty: String,
    val unitCode: String

)
