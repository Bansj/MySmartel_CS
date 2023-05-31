package com.smartel.mysmartel_ver_1

interface LoginCallback {
    fun onLoginSuccess(response: LoginResponse)
    fun onLoginFailure(error: String)
}
