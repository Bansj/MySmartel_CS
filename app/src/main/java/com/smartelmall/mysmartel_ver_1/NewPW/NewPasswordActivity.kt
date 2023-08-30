package com.smartelmall.mysmartel_ver_1.NewPW

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartelmall.mysmartel_ver_1.LoginActivity
import com.smartelmall.mysmartel_ver_1.R
import com.smartelmall.mysmartel_ver_1.SettingFragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var serviceNum: String
    private val url = "https://www.mysmartel.com/smartel/api_set_passwd.php"
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        serviceNum = intent.getStringExtra("phoneNumber") ?: ""
        Log.d("------------NewPasswordActivity check phoneNumber","get phoneNumber: $serviceNum--------")

        val editNewPassword = findViewById<EditText>(R.id.edit_newPassword)
        val editPasswordCheck = findViewById<EditText>(R.id.edit_passwordCheck)
        val btnCheck = findViewById<Button>(R.id.btn_check)

        btnCheck.visibility = View.INVISIBLE
        // Create a new TextWatcher for both EditTexts
        val passwordTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Remove any previous callbacks
                runnable?.let { handler.removeCallbacks(it) }

                runnable = Runnable {
                    if (isValidPassword(editNewPassword.text.toString()) &&
                        editNewPassword.text.toString() == editPasswordCheck.text.toString()) {
                        btnCheck.visibility = View.VISIBLE

                        // edit_passwordCheck에 입력이 있는 경우에만 토스트 메시지를 표시합니다.
                        if (editPasswordCheck.text.isNotBlank()) {
                            // "일치합니다." 메시지 표시
                            showToastMessage("일치합니다.")
                        }
                    } else {
                        btnCheck.visibility = View.INVISIBLE
                        if (editNewPassword.text.isNotBlank() && editPasswordCheck.text.isNotBlank()) {
                            // "일치하지 않습니다." 메시지 표시
                            showToastMessage("일치하지 않습니다.")
                        }

                        // 각 조건을 확인하고 어떤 요소가 누락되었는지 확인합니다.
                        val newPassword = editNewPassword.text.toString()
                        val hasEnglish = newPassword.any { it.isLetter() }
                        val hasNumber = newPassword.any { it.isDigit() }
                        val hasSpecialCharacter = newPassword.any { !it.isLetterOrDigit() }

                        if (!hasEnglish) {
                            showToastMessage("영어 포함되지 않았습니다.")
                        }

                        if (!hasNumber) {
                            showToastMessage("숫자 포함되지 않았습니다.")
                        }

                        if (!hasSpecialCharacter) {
                            showToastMessage("특수문자 포함되지 않았습니다.")
                        }
                    }
                }

                // Delay the execution of the runnable by 500 milliseconds
                handler.postDelayed(runnable!!, 800)
            }

        }


        // Set the text watchers to both EditTexts
        editNewPassword.addTextChangedListener(passwordTextWatcher)
        editPasswordCheck.addTextChangedListener(passwordTextWatcher)

        // 확인버튼 클릭 이벤트
        btnCheck.setOnClickListener{
            setNewPasswd(serviceNum,editNewPassword.text.toString())
        }
        // 뒤로가기 버튼 클릭 이벤트
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun isValidPassword(password:String):Boolean{
        // Password validation logic here.
        // You can add more complex validation logic as per your requirements.
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$"
        return Pattern.compile(passwordPattern).matcher(password).matches()
    }
    private fun setNewPasswd(serviceNum:String,newPasswd:String){
        // This is just a simple example of how to use OkHttp to make a POST request.
        // In real applications you should consider using Retrofit or another more robust HTTP client.

        val client= OkHttpClient()

        val jsonMediaType= "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonObject= JSONObject().apply{
            put("serviceNum",serviceNum)
            put("passwd",newPasswd)
        }

        Log.d("API_LOG", "Service number: $serviceNum, New password: $newPasswd")

        val requestBody=jsonObject.toString().toRequestBody(jsonMediaType)

        Request.Builder().url(url).post(requestBody).build().also { request->
            client.newCall(request).enqueue(object :Callback{
                override fun onFailure(call :Call,e :IOException){
                    e.printStackTrace()
                    Log.e("API_LOG", "Request failed with exception: ${e.message}")
                }

                override fun onResponse(call :Call,response :Response){
                    response.body?.string()?.let {responseBody->
                        JSONObject(responseBody)?.getString("ResultCode")?.let{resultCode->
                            if(resultCode=="0000"){
                                // Password update was successful
                                runOnUiThread {
                                    showToastMessage("비밀번호 변경완료")
                                    val intent = Intent(this@NewPasswordActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                Log.i("API_LOG", "Password update was successful")
                            }else{
                                // Password update failed
                                runOnUiThread {
                                    showToastMessage("비밀번호 변경 실패")
                                }
                                Log.i("API_LOG", "Password update failed with resultCode: $resultCode")
                            }
                        }
                    } ?: run {
                        Log.e("API_LOG", "Response body is null or empty.")
                    }
                }

            })
            Log.d("API_LOG","Request sent to URL: $url with body: ${requestBody.contentLength()}")

        }

    }

    private fun showToastMessage(message:String){
        Toast.makeText(this@NewPasswordActivity,message,Toast.LENGTH_SHORT).apply{
            setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0 ,600 )
            show()
        }
    }

}




