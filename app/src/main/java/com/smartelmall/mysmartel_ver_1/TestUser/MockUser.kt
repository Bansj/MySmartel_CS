package com.smartelmall.mysmartel_ver_1.TestUser

data class MockUser(
    val telecom: String,
    val serviceAcct: String,
    val custName: String,
    val phoneNumber: String
)

val mockUser = MockUser("LGT", "500279120526", "김지은", "01033504523")
