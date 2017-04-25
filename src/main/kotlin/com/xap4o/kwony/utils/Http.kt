package com.xap4o.kwony.utils

import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import io.vertx.core.MultiMap
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
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

inline fun <reified T> HttpClient.json(req: HttpRequest): CompletableFuture<T> =
        execute(req).map { it.bodyAsJson(T::class.java) }

fun <T> List<CompletableFuture<T>>.gatherUnordered(): CompletableFuture<List<T>> {
    return CompletableFuture.allOf(*this.toTypedArray()).thenApply { _ -> this.map { it.join()} }
}

fun <T> CompletableFuture<T>.materialize(): CompletableFuture<Try<T>> {
    val future = CompletableFuture<Try<T>>()
    handle { result, throwable ->
        future.complete(if (throwable != null) Failure(throwable) else Success(result))
    }
    return future
}
fun <T> CompletableFuture<Try<T>>.dematerialize(): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    this.thenApply {
        when (it) {
            is Success<T> -> future.complete(it.value)
            is Failure<*> -> future.completeExceptionally(it.error)
        }
    }
    return future
}

fun <A, B> CompletableFuture<A>.map(f: (A) -> B): CompletableFuture<B> = thenApply(f)

fun <A, B> CompletableFuture<A>.flatMap(f: (A) -> CompletableFuture<B>): CompletableFuture<B> = thenCompose(f)

sealed class Try<T> {
    abstract fun isSuccess(): Boolean

    fun onError(f: (Throwable) -> Unit) = if (this is Failure) f(error) else Unit

    companion object {
        operator fun <T> invoke(f: () -> T): Try<T> = try { Success(f()) } catch (e: Throwable) { Failure(e) }
    }
}
data class Success<T>(val value: T) : Try<T>() {
    override fun isSuccess() = true
}
data class Failure<T>(val error: Throwable) : Try<T>() {
    override fun isSuccess() = false
}

