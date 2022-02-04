package com.imherrera.composeweb

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings.LOAD_NO_CACHE
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imherrera.composeweb.ui.theme.ComposeWebTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeWebTheme {
                ComposeWebSample()
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ComposeWebSample() {
    val webState = rememberWebState {
        settings.javaScriptEnabled = true
        settings.cacheMode = LOAD_NO_CACHE
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp),
        ) {
            if (webState.loading) LinearProgressIndicator()
        }
        WebComponent(
            modifier = Modifier.fillMaxSize(),
            state = webState,
        )
    }

    LaunchedEffect(Unit) {
        webState.load("https://www.google.com")
        delay(5000)
        webState.load("https://www.bing.com")
        delay(5000)
        webState.load("https://www.youtube.com")
    }
}
