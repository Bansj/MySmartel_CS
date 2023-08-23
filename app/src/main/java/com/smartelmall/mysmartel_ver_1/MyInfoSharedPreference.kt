package com.smartelmall.mysmartel_ver_1

import android.content.Context
import android.content.SharedPreferences

class MyInfoSharedPreferences(private val context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences("MyInfoPrefs", Context.MODE_PRIVATE)


    companion object {
        private const val PREF_NAME = "my_info_prefs"
        private const val KEY_CUST_NAME = "cust_name"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_TELECOM = "telecom"
        private const val KEY_SERVICE_ACCT = "serviceAcct"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var custName: String?
        get() = preferences.getString(KEY_CUST_NAME, "")?:""
        set(value) = preferences.edit().putString(KEY_CUST_NAME, value).apply()

    var phoneNumber: String?
        get() = preferences.getString(KEY_PHONE_NUMBER, "")?:""
        set(value) = preferences.edit().putString(KEY_PHONE_NUMBER, value).apply()

    var telecom: String?
        get() = preferences.getString(KEY_TELECOM, "")?:""
        set(value) = preferences.edit().putString(KEY_TELECOM, value).apply()

    var serviceAcct: String?
        get() = preferences.getString(KEY_SERVICE_ACCT, "")?:""
        set(value) = preferences.edit().putString(KEY_SERVICE_ACCT, value).apply()
}
