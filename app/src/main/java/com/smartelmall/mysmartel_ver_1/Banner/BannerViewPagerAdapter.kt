package com.smartelmall.mysmartel_ver_1.Banner

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R


// 광고 배너 어댑터
class BannerViewPagerAdapter(private val context: Context, private val bannerList: List<BannerItem>) :
    RecyclerView.Adapter<BannerViewPagerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val webView: WebView = view.findViewById(R.id.webView)

        init {
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()


            webView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    bannerList[adapterPosition].imageLink?.let { openUrlInBrowser(it) }
                    return@setOnTouchListener true
                }
                false
            }
        }

        fun bind(bannerItem: BannerItem) {
            val imageUrl = bannerItem.imagePath
            val htmlContent = """
        <html>
            <head>
                <style>
                    img {
                        max-width: 100%;
                        height: auto;
                        border-radius: 15px; /* Add this line */
                    }
                    body {
                        margin: 0;
                        padding: 0;
                    }
                </style>
            </head>
            <body>
                <img src="$imageUrl">
            </body>
        </html>"""
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.banner_item, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(bannerList[position])
    }

    override fun getItemCount(): Int = bannerList.size

    private fun openUrlInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // 사용 가능한 인터넷 브라우저 앱이 없는 경우 오류 메시지를 표시
            Toast.makeText(context, "인터넷 브라우저를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
