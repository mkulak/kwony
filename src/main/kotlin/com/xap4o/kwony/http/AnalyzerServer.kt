package com.xap4o.kwony.http

import io.vertx.core.http.HttpMethod

object AnalyzerServer : HttpApi({
    route(HttpMethod.POST, "/analyze") { request ->
        val tweetJson = request.body.toJsonObj()
        jsonResponse((tweetJson.getString("text").length % 2 == 0).toString())
    }
})


