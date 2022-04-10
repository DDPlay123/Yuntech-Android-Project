package com.ddplay.yuntech.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import com.ddplay.yuntech.Common.CommonData
import com.ddplay.yuntech.R

class WebActivity : AppCompatActivity() {
    private lateinit var edUrl: EditText
    private lateinit var webView: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        edUrl = findViewById(R.id.ed_url)
        webView = findViewById(R.id.webView)

        edUrl.setText(CommonData().url)
        searchWeb(edUrl.text.toString())

        edUrl.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                searchWeb(edUrl.text.toString())
            }
            false
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun searchWeb(url: String) {
        // WebView的初始設定。
        val webSettings: WebSettings = webView.settings
        // WebView可支援JavaScript。
        webSettings.javaScriptEnabled = true
        // 調整網頁大小，使WebView可完整顯示網頁。
        webView.settings.useWideViewPort = true
        // 設定WebView 支援載入更多格式頁面
        webView.settings.loadWithOverviewMode = true
        // 用於載入網頁過程的監聽。
        webView.webViewClient = WebViewClient()
        // 初始畫面。
        webView.loadUrl(url)
    }

    fun back(view: View) {
        super.onBackPressed()
    }
}