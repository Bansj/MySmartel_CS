package com.smartelmall.mysmartel_ver_1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.gson.GsonBuilder
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        serviceNum = intent.getStringExtra("serviceNum") ?: ""

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

                        // Display the message to user when passwords match.
                        showToastMessage("Passwords match")

                    } else {
                        btnCheck.visibility = View.INVISIBLE
                        if(editNewPassword.text.toString() != editPasswordCheck.text.toString()){
                            // Display the message to user when passwords do not match.
                            showToastMessage("Passwords do not match")
                        }
                    }
                }

                // Delay the execution of the runnable by 500 milliseconds
                handler.postDelayed(runnable!!, 500)
            }

        }

        // Set the text watchers to both EditTexts
        editNewPassword.addTextChangedListener(passwordTextWatcher)
        editPasswordCheck.addTextChangedListener(passwordTextWatcher)

        btnCheck.setOnClickListener{
            setNewPasswd(serviceNum,editNewPassword.text.toString())
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
        val requestBody=jsonObject.toString().toRequestBody(jsonMediaType)

        Request.Builder().url(url).post(requestBody).build().also { request->
            client.newCall(request).enqueue(object :Callback{
                override fun onFailure(call :Call,e :IOException){
                    e.printStackTrace()
                }

                override fun onResponse(call :Call,response :Response){
                    response.body?.string()?.let {responseBody->
                        JSONObject(responseBody)?.getString("ResultCode")?.let{resultCode->
                            if(resultCode=="0000"){
                                // Password update was successful
                            }else{
                                // Password update failed
                            }
                        }
                    }
                }

            })
        }

    }
    private fun showToastMessage(message:String){
        Toast.makeText(this@NewPasswordActivity,message,Toast.LENGTH_SHORT).apply{
            setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0 ,600 )
            show()
        }
    }

}




