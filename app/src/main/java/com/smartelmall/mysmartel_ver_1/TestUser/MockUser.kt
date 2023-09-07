package com.smartelmall.mysmartel_ver_1.TestUser

data class MockUser(
    val telecom: String,
    val serviceAcct: String,
    val custName: String,
    val phoneNumber: String
)

val mockUser = MockUser("SKT", "113971932", "반승주", "01075244523")
