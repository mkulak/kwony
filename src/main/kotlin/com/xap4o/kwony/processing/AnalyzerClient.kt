package com.xap4o.kwony.processing

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import com.xap4o.kwony.http.Json
import com.xap4o.kwony.twitter.Tweet
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod

interface AnalyzerClient {
    fun analyze(tweet: Tweet): Future<Boolean>
}

class AnalyzerClientImpl(val config: ProcessingConfig, val http: HttpClient) : AnalyzerClient {

    override fun analyze(tweet: Tweet): Future<Boolean> {
        val req = HttpRequest("${config.analyzeHost}/analyze", HttpMethod.POST)
                .withBody(Json(tweet))
                .withTimeout(config.timeout)
        return http.make(req, Boolean::class.java)
    }
}
