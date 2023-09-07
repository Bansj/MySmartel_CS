package com.smartelmall.mysmartel_ver_1.TestUser

import androidx.privacysandbox.tools.core.model.Types.any
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smartelmall.mysmartel_ver_1.LoginActivity
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*
@RunWith(RobolectricTestRunner::class)
class LoginActivityTest {

    private lateinit var loginActivity: LoginActivity
    private lateinit var requestQueueMock: RequestQueue

    @Before
    fun setup() {
        loginActivity = LoginActivity()
        requestQueueMock = mockk(relaxed = true)
        loginActivity.requestQueue = requestQueueMock
    }

    @Test
    fun testLoginUser() {
        val phoneNumberSlot = slot<String>()
        val passwordSlot = slot<String>()

        // 모의 API 응답을 설정
        val mockApiResponse = JSONObject()
        mockApiResponse.put("resultCd", "true")
        mockApiResponse.put("typ", "pwd")

        every {
            requestQueueMock.add(capture(slot()))
        } answers {
            val requestListener = slot<Response.Listener<JSONObject>>()
            requestListener.captured.onResponse(mockApiResponse)
        }

        // 모의 사용자 정보를 설정
        loginActivity.phoneNumberEditText.text = mockUser.phoneNumber
        loginActivity.passwordEditText.text = "test"

        // 로그인 메서드 호출
        loginActivity.loginUser()

        // API 호출 검증
        verify {
            requestQueueMock.add(withArg {
                val requestBody = JSONObject(it.body.toString())
                phoneNumberSlot.captured = requestBody.getString("log_id")
                passwordSlot.captured = requestBody.getString("log_pwd")
            })
        }

        // 사용자 정보 검증
        assert(phoneNumberSlot.captured == mockUser.phoneNumber)
        assert(passwordSlot.captured == "test")
    }
}*/
