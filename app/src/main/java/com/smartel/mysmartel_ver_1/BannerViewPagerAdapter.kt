package com.smartel.mysmartel_ver_1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mysmartel_ver_1.R

class BannerViewPagerAdapter(private val context: Context, private val bannerList: List<BannerItem>) :
    RecyclerView.Adapter<BannerViewPagerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val webView: WebView = view.findViewById(R.id.webView)

        init {
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()
        }

        fun bind(bannerItem: BannerItem) {
            webView.loadUrl(bannerItem.imageLink)
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
}
