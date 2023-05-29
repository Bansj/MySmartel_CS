package com.example.mysmartel_ver_1

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

import com.android.volley.Request

interface ApiService {
    fun performLogin(url: String, log_id: String, log_pwd: String, onResponse:
        (ApiResponse) -> Unit, onError: (Exception) -> Unit)
}
