package com.xap4o.kwony.utils

import io.vertx.core.Future
import io.vertx.core.MultiMap
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import java.util.Base64
import java.util.concurrent.ConcurrentLinkedQueue

fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
}

fun Map<String, String>.toMultiMap(): MultiMap {
    val self = this
    return MultiMap.caseInsensitiveMultiMap().apply { addAll(self) }
}

fun String.encodeBase64(): String =
        Base64.getEncoder().encode(this.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)

fun basicAuthHeader(name: String, password: String): Pair<String, String> =
        "Authorization" to ("Basic " + ("$name:$password".encodeBase64()))

fun oauth2Header(token: String): Pair<String, String> =
        "Authorization" to "Bearer $token"

fun <T> List<Future<T>>.gatherUnordered(): Future<List<T>> {
    val results = ConcurrentLinkedQueue<T>()
    val future = Future.future<List<T>>()
    fun addResult(t: T) {
        results.add(t)
        if (results.size == size) {  //TODO: fix race condition
            future.complete(results.toList())
        }
    }
    forEach {
        it.setHandler { res ->
            if (res.succeeded()) {
                addResult(res.result())
            } else {
                future.fail(res.cause())
            }
        }
    }
    return future
}

fun <T> Future<T>.materialize(): Future<Try<T>> {
    val future = Future.future<Try<T>>()
    setHandler {
        future.complete(if (it.succeeded()) Success(it.result()) else Failure(it.cause()))
    }
    return future
}

//fun <A, B> Future<A>.flatMap(f: (A) -> Future<B>): Future<B> = compose(f)

sealed class Try<T> {
    abstract fun isSuccess(): Boolean
}
data class Success<T>(val value: T) : Try<T>() {
    override fun isSuccess() = true
}
data class Failure<T>(val error: Throwable) : Try<T>() {
    override fun isSuccess() = false
}

