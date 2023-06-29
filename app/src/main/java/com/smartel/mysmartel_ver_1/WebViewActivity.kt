package com.smartel.mysmartel_ver_1


import android.content.Intent
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.widget.Button
import com.example.mysmartel_ver_1.R


class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var toLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = MyWebViewClient()
        webView.loadUrl("https://www.mysmartel.com/page/user_login.php")

        toLoginButton = findViewById(R.id.btn_toLogin)
        toLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.proceed() // Ignore SSL certificate errors
        }
    }
}

