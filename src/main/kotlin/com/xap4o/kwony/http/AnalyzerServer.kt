package com.xap4o.kwony.http

import com.xap4o.kwony.utils.endWithJson
import com.xap4o.kwony.utils.post
import io.vertx.core.json.JsonObject

object AnalyzerServer : HttpApi({
    post("/analyze") { ctx, request, response ->
        request.bodyHandler { buffer ->
            val tweetJson = JsonObject(buffer.toString())
            response.endWithJson((tweetJson.getString("text").length % 2 == 0).toString())
        }
    }
})


