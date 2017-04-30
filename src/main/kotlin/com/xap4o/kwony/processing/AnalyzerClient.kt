package com.xap4o.kwony.processing

import com.xap4o.kwony.config.AnalyzeConfig
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import com.xap4o.kwony.http.Json
import com.xap4o.kwony.http.json
import com.xap4o.kwony.twitter.Tweet
import com.xap4o.kwony.utils.Try
import io.vertx.core.http.HttpMethod

interface AnalyzerClient {
    suspend fun analyze(tweet: Tweet): Try<Boolean>
}

class AnalyzerClientImpl(val config: AnalyzeConfig, val http: HttpClient) : AnalyzerClient {

    override suspend fun analyze(tweet: Tweet): Try<Boolean> {
        val req = HttpRequest("${config.host}/analyze", HttpMethod.POST)
                .withBody(Json(tweet))
                .withTimeout(config.timeout)
        return http.json<Boolean>(req).withErrorMessage("Failed to analyze tweet $tweet")
    }
}
