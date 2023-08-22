package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.mysmartel_ver_1.R

class BannerWebViewFragment : Fragment() {
    private var imageLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageLink = it.getString(ARG_IMAGE_LINK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_banner_webview, container, false)
        val webView: WebView = view.findViewById(R.id.i_bannerWebview)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        val htmlContent = "<html><head><style>img{max-width: 100%; height: auto;}</style></head><body><img src=\"$imageLink\"></body></html>"
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        return view
    }

    companion object {
        private const val ARG_IMAGE_LINK = "arg_image_link"

        fun newInstance(imageLink: String): BannerWebViewFragment {
            val fragment = BannerWebViewFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_LINK, imageLink)
            fragment.arguments = args
            return fragment
        }
    }
}
