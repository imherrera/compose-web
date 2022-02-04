package com.imherrera.composeweb

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import androidx.webkit.WebViewFeature.VISUAL_STATE_CALLBACK
import kotlin.random.Random

sealed class WebContent(val doReload: Boolean) {
    data class Url(
        val url: String,
        val headers: Map<String, String>?,
        val reload: Boolean = false
    ) : WebContent(reload)

    data class Data(
        val data: String,
        val mimeType: String?,
        val encoding: String?,
        val baseUrl: String?,
        val historyUrl: String?,
        val reload: Boolean = false
    ) : WebContent(reload)
}

open class WebStateClient(
    val config: WebView.() -> Unit,
    private val random: Random = Random(Long.MAX_VALUE),
    private var requestId: Long = random.nextLong()
) : WebViewClient() {
    var loading by mutableStateOf(true)
        private set

    var content by mutableStateOf<WebContent?>(null)

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        loading = true
        requestId = random.nextLong()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (view?.progress ?: 0 < 100) return
        if (WebViewFeature.isFeatureSupported(VISUAL_STATE_CALLBACK)) {
            val callback = WebViewCompat.VisualStateCallback { responseId ->
                if (responseId == requestId) {
                    loading = false
                }
            }
            WebViewCompat.postVisualStateCallback(view!!, requestId, callback)
        } else {
            loading = false
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request != null) content = WebContent.Url(request.url.toString(), request.requestHeaders)
        return true
    }

    fun load(url: String, headers: Map<String, String>? = null) {
        content = WebContent.Url(url, headers)
    }

    fun load(
        data: String,
        mimeType: String? = null,
        encoding: String? = null,
        baseUrl: String? = null,
        historyUrl: String? = null
    ) {
        content = WebContent.Data(data, mimeType, encoding, baseUrl, historyUrl)
    }

    fun reload() {
        content = when (val content = content) {
            is WebContent.Data -> content.copy(reload = true)
            is WebContent.Url -> content.copy(reload = true)
            null -> return
        }
    }
}

@Composable
fun rememberWebState(config: WebView.() -> Unit): WebStateClient = remember {
    WebStateClient(config)
}