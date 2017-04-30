package com.xap4o.kwony.utils

import io.vertx.core.MultiMap
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import java.net.URL
import java.util.Base64

fun HttpServer.start(router: Router, host: String) {
    val url = URL(host)
    requestHandler(router::accept).listen(url.port, url.host)
}

fun HttpServerResponse.endWithJson(obj: Any) {
    putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
}

fun Map<String, String>.toMultiMap(): MultiMap = MultiMap.caseInsensitiveMultiMap().apply { addAll(this@toMultiMap) }

fun String.encodeBase64(): String =
        Base64.getEncoder().encode(this.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)

fun basicAuthHeader(name: String, password: String): Pair<String, String> =
        "Authorization" to ("Basic " + ("$name:$password".encodeBase64()))

fun oauth2Header(token: String): Pair<String, String> =
        "Authorization" to "Bearer $token"


