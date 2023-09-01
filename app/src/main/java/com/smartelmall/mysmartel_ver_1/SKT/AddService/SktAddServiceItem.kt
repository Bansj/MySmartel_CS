package com.smartelmall.mysmartel_ver_1.SKT.AddService

data class SktAddServiceItem(
    val opClCd: String,
    val opTypCd: String,
    val svcNum: String,
    val svAcntNum: String,
    val prodRecCnt: Int,
    val products: List<Product>
) {
    data class Product(
        val prodId: String,
        val prodScrbDt: String,
        val prodNm: String,
        var displayProdFee: String
    )
}
