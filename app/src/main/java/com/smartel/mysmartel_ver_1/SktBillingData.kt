package com.smartel.mysmartel_ver_1

data class SktBillingData(
    val title: String,
    val description: String
)
data class BillItem(val title: String, val amount: String)
data class BillDetails(val basicCharges: List<BillItem>, val additionalCharges: List<BillItem>, val totalTax: String)
data class BillDetailItem(val title: String, val value: String)
