package com.smartel.mysmartel_ver_1

interface ApiService {
    fun performLogin(url: String, log_id: String, log_pwd: String, onResponse:
        (ApiResponse) -> Unit, onError: (Exception) -> Unit)
}
