package com.smartel.mysmartel_ver_1

import android.content.Context
import android.content.SharedPreferences

class MyInfoSharedPreferences(private val context: Context) {

    companion object {
        private const val PREF_NAME = "my_info_prefs"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_TELECOM = "telecom"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var userName: String?
        get() = preferences.getString(KEY_USER_NAME, null)
        set(value) = preferences.edit().putString(KEY_USER_NAME, value).apply()

    var phoneNumber: String?
        get() = preferences.getString(KEY_PHONE_NUMBER, null)
        set(value) = preferences.edit().putString(KEY_PHONE_NUMBER, value).apply()

    var telecom: String?
        get() = preferences.getString(KEY_TELECOM, null)
        set(value) = preferences.edit().putString(KEY_TELECOM, value).apply()
}
