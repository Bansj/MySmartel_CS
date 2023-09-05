package com.smartelmall.mysmartel_ver_1.KT.Deduct

import org.json.JSONArray
import org.json.JSONObject

data class KtDeductRequestBody(
    val header: List<Header>,
    val body: List<Body>
) {
    data class Header(
        val type: String
    )

    data class Body(
        val traceno: String,
        val custId: String,
        val ncn: String,
        val ctn: String,
        val clientIp: String,
        val userId: String,
        val useMonth: String
    )

    fun toJsonString(): String {
        val jsonObject = JSONObject()
        val headerArray = JSONArray()
        val bodyArray = JSONArray()

        for (header in header) {
            val headerObject = JSONObject()
            headerObject.put("type", header.type)
            headerArray.put(headerObject)
        }

        for (body in body) {
            val bodyObject = JSONObject()
            bodyObject.put("traceno", body.traceno)
            bodyObject.put("custId", body.custId)
            bodyObject.put("ncn", body.ncn)
            bodyObject.put("ctn", body.ctn)
            bodyObject.put("clientIp", body.clientIp)
            bodyObject.put("userId", body.userId)
            bodyObject.put("useMonth", body.useMonth)
            bodyArray.put(bodyObject)
        }

        jsonObject.put("header", headerArray)
        jsonObject.put("body", bodyArray)

        return jsonObject.toString()
    }
}
