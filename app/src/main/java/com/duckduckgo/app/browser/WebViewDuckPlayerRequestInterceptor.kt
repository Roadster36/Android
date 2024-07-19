/*
 * Copyright (c) 2024 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.browser

import android.net.Uri
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.core.net.toUri
import com.duckduckgo.common.utils.DispatcherProvider
import com.duckduckgo.di.scopes.AppScope
import com.duckduckgo.duckplayer.api.DUCK_PLAYER_ASSETS_PATH
import com.duckduckgo.duckplayer.api.DuckPlayer
import com.squareup.anvil.annotations.ContributesBinding
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.withContext

interface WebViewDuckPlayerRequestInterceptor {
    suspend fun intercept(
        request: WebResourceRequest,
        url: Uri,
        webView: WebView,
    ): WebResourceResponse?
}

@ContributesBinding(AppScope::class)
class RealWebViewDuckPlayerRequestInterceptor @Inject constructor(
    private val duckPlayer: DuckPlayer,
    private val mimeTypeMap: MimeTypeMap,
    private val dispatchers: DispatcherProvider,
) : WebViewDuckPlayerRequestInterceptor {

    override suspend fun intercept(
        request: WebResourceRequest,
        url: Uri,
        webView: WebView,
    ): WebResourceResponse? {
        if (duckPlayer.isDuckPlayerUri(url)) {
            return processDuckPlayerUri(url, webView)
        } else if (duckPlayer.isYoutubeWatchUrl(url)) {
            return processYouTubeWatchUri(request, url, webView)
        } else if (duckPlayer.isSimulatedYoutubeNoCookie(url)) {
            return processSimulatedYouTubeNoCookieUri(url, webView)
        }

        return null
    }
    private fun processSimulatedYouTubeNoCookieUri(
        url: Uri,
        webView: WebView,
    ): WebResourceResponse {
        val path = duckPlayer.getDuckPlayerAssetsPath(url)
        val mimeType = mimeTypeMap.getMimeTypeFromExtension(path?.substringAfterLast("."))

        if (path != null && mimeType != null) {
            try {
                val inputStream: InputStream = webView.context.assets.open(path)
                return WebResourceResponse(mimeType, "UTF-8", inputStream)
            } catch (e: Exception) {
                return WebResourceResponse(null, null, null)
            }
        } else {
            val inputStream: InputStream = webView.context.assets.open(DUCK_PLAYER_ASSETS_PATH)
            return WebResourceResponse("text/html", "UTF-8", inputStream)
        }
    }

    private suspend fun processYouTubeWatchUri(
        request: WebResourceRequest,
        url: Uri,
        webView: WebView,
    ): WebResourceResponse? {
        val referer = request.requestHeaders["Referer"]
        val previousUrl = url.getQueryParameter("embeds_referring_euri")
        if ((referer != null && duckPlayer.isSimulatedYoutubeNoCookie(referer.toUri())) ||
            (previousUrl != null && duckPlayer.isSimulatedYoutubeNoCookie(previousUrl))
        ) {
            withContext(dispatchers.main()) {
                url.getQueryParameter("v")?.let {
                    webView.loadUrl("duck://player/openInYoutube?v=$it")
                }
            }
            return WebResourceResponse(null, null, null)
        } else if (duckPlayer.shouldNavigateToDuckPlayer()) {
            withContext(dispatchers.main()) {
                webView.loadUrl(duckPlayer.createDuckPlayerUriFromYoutube(url))
            }
            return WebResourceResponse(null, null, null)
        }
        return null
    }

    private suspend fun processDuckPlayerUri(
        url: Uri,
        webView: WebView,
    ): WebResourceResponse {
        if (url.pathSegments?.firstOrNull()?.equals("openInYoutube", ignoreCase = true) == true) {
            duckPlayer.createYoutubeWatchUrlFromDuckPlayer(url)?.let { youtubeUrl ->
                duckPlayer.youTubeRequestedFromDuckPlayer()
                withContext(dispatchers.main()) {
                    webView.loadUrl(youtubeUrl)
                }
            }
        } else {
            duckPlayer.createYoutubeNoCookieFromDuckPlayer(url)?.let { youtubeUrl ->
                withContext(dispatchers.main()) {
                    webView.loadUrl(youtubeUrl)
                }
            }
        }
        return WebResourceResponse(null, null, null)
    }
}
