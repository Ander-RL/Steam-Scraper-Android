package com.arl.steamscraper

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class WebViewActivity : AppCompatActivity() {

    lateinit var webView: WebView
    lateinit var gameUrl: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webView = findViewById(R.id.webview)
        val fab: ExtendedFloatingActionButton = findViewById(R.id.btn_fab_webview)

        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        webView.loadUrl("https://store.steampowered.com")
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {}
        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                supportActionBar?.title = title
            }
        }

        fab.setOnClickListener {
            gameUrl = webView.url.toString()

            if (gameUrl.contains("https://store.steampowered.com/app/") and gameUrl.isNotEmpty()) {
                val intent = intent
                intent.putExtra("url", gameUrl)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Please select a correct URL", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.webview_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_btn_back -> onBackPressed()
            R.id.menu_btn_forth -> onForthPressed()
            R.id.menu_btn_refresh -> webView.reload()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onForthPressed() {
        if (webView.canGoForward()) {
            webView.goForward()
        }
    }
}