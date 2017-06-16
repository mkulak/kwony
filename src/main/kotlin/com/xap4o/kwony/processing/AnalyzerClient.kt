package com.xap4o.kwony.processing

import com.xap4o.kwony.config.AnalyzeConfig
import com.xap4o.kwony.http.*
import com.xap4o.kwony.twitter.Tweet
import com.xap4o.kwony.utils.*
import io.vertx.core.http.HttpMethod

interface AnalyzerClient {
    fun analyze(tweet: Tweet): Future<Boolean>
}

class AnalyzerClientImpl(val config: AnalyzeConfig, val http: HttpClient) : AnalyzerClient {

    override fun analyze(tweet: Tweet): Future<Boolean> {
        val req = HttpRequest(config.host.withPath("/analyze"), HttpMethod.POST)
                .withBody(Json(tweet))
                .withTimeout(config.timeout)
        return http.json<Boolean>(req).withErrorMessage("Failed to analyze tweet $tweet")
    }
}
