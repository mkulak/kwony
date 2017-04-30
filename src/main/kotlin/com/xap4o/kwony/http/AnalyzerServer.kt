package com.xap4o.kwony.http

import com.xap4o.kwony.utils.endWithJson
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

object AnalyzerServer {
    fun api(vertx: Vertx): Router =
            Router.router(vertx).apply {
                route(HttpMethod.POST, "/analyze").handler(BodyHandler.create())
                route(HttpMethod.POST, "/analyze").handler { context ->
                    val tweetJson = context.bodyAsJson
                    context.response().endWithJson((tweetJson.getString("text").length % 2 == 0).toString())
                }
            }
}


