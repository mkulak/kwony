package com.xap4o.kwony.utils

import io.vertx.core.MultiMap
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import java.net.URL
import java.util.Base64

fun HttpServer.start(router: Router, url: URL) = requestHandler(router::accept).listen(url.port, url.host)

fun HttpServerResponse.endWithJson(obj: Any) {
    putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
}

fun Map<String, String>.toMultiMap(): MultiMap = MultiMap.caseInsensitiveMultiMap().apply { addAll(this@toMultiMap) }

fun MultiMap.toNormalMap(): Map<String, String> = asIterable().map { (k, v) -> k to v }.toMap()

fun String.encodeBase64(): String =
        Base64.getEncoder().encode(this.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)

fun basicAuthHeader(name: String, password: String): Pair<String, String> =
        "Authorization" to ("Basic " + ("$name:$password".encodeBase64()))

fun oauth2Header(token: String): Pair<String, String> =
        "Authorization" to "Bearer $token"

fun URL.withPath(path: String): URL = URL("$this$path")

fun Router.routeWithBody(method: HttpMethod, path: String): Route {
    route(method, path).handler(BodyHandler.create())
    return route(method, path)
}


