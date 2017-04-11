package com.xap4o.kwony.processing

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.twitter.Tweet
import java.util.concurrent.Future

interface AnalyzerClient {
    fun analyze(tweet: Tweet): Future<Boolean>
}

class AnalyzerClientImpl(config: ProcessingConfig, http: HttpClient) : AnalyzerClient {

    override fun analyze(tweet: Tweet): Future<Boolean> {
//    val req: HttpRequest = HttpRequest()
//      .withUri(s"${config.analyzeHost}/analyze")
//      .withMethod(HttpMethods.POST)
//      .withEntity(HttpEntity(ContentTypes.`application/json`, tweet.toJson.compactPrint))
//
//    http.make[Boolean](req, config.timeout)
        TODO()
    }
}
