package com.xap4o.kwony.utils

import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.client.HttpResponse
import java.util.Base64
import java.util.concurrent.CompletableFuture

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

fun HttpClient.expect(req: HttpRequest): CompletableFuture<HttpResponse<Buffer>> =
        execute(req).map {
            if (it.statusCode() in 200..299) it
            else throw RuntimeException("Unexpected return code: ${it.statusCode()}")
        }

inline fun <reified T> HttpClient.json(req: HttpRequest): CompletableFuture<T> =
        expect(req).map { it.bodyAsJson(T::class.java) }

