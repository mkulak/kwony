package com.xap4o.kwony.http

import com.xap4o.kwony.utils.endWithJson
import com.xap4o.kwony.utils.routeWithBody
import io.vertx.core.http.HttpMethod

object AnalyzerServer : HttpApi({
    routeWithBody(HttpMethod.POST, "/analyze").handler { context ->
        val tweetJson = context.bodyAsJson
        context.response().endWithJson((tweetJson.getString("text").length % 2 == 0).toString())
    }
})


