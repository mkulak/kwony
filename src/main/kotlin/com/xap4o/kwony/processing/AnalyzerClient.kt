package com.xap4o.kwony.processing

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import com.xap4o.kwony.http.Json
import com.xap4o.kwony.twitter.Tweet
import com.xap4o.kwony.utils.Try
import com.xap4o.kwony.utils.await
import com.xap4o.kwony.utils.json
import io.vertx.core.http.HttpMethod

interface AnalyzerClient {
    suspend fun analyze(tweet: Tweet): Try<Boolean>
}

class AnalyzerClientImpl(val config: ProcessingConfig, val http: HttpClient) : AnalyzerClient {

    override suspend fun analyze(tweet: Tweet): Try<Boolean> {
        val req = HttpRequest("${config.analyzeHost}/analyze", HttpMethod.POST)
                .withBody(Json(tweet))
                .withTimeout(config.timeout)
        return http.json<Boolean>(req).await()
    }
}
