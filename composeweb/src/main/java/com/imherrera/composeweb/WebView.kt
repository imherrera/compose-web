package com.imherrera.composeweb

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebView(modifier: Modifier = Modifier, state: WebStateClient) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                state.config(this)
                webViewClient = state
            }
        },
        update = {
            it.loadContent(state.content)
        }
    )
}

private fun WebView.loadContent(content: WebContent?) {
    when {
        content == null -> return
        content.doReload -> reload()
        content is WebContent.Data -> {
            val (data, mimeType, encoding, baseUrl, historyUrl) = content
            if (baseUrl != null) {
                loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
            } else {
                loadData(data, mimeType, encoding)
            }
        }
        content is WebContent.Url &&
                /**
                 * We cant just directly compare both strings because [WebView.getUrl] for some reason
                 * appends a / character at the end, and equals will return false in that case therefore
                 * triggering a loop
                 * */
                (content.url.removeSuffix("/") != url?.removeSuffix("/")) -> {
            val (url, headers) = content
            if (headers != null) {
                loadUrl(url, headers)
            } else {
                loadUrl(url)
            }
        }
    }
}