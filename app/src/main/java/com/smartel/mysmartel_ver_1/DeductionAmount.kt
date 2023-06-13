package com.smartel.mysmartel_ver_1

data class DeductionAmount(
    val productId: String,
    val productName: String,
    val deductionCode: String,
    val deductionName: String,
    val basicDeductionAmount: String,
    val usage: String,
    val remainingAmount: String,
    val deductionUnitCode: String
)
