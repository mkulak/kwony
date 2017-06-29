package com.xap4o.kwony.utils

import java.util.concurrent.CompletableFuture
import kotlin.coroutines.experimental.suspendCoroutine

typealias Future<T> = CompletableFuture<T>

fun <T> List<Future<T>>.gatherUnordered(): Future<List<T>> {
    return Future.allOf(*this.toTypedArray()).thenApply { _ -> this.map { it.join() } }
}

fun <T> Future<T>.materialize(): Future<Try<T>> {
    val future = Future<Try<T>>()
    handle { result, throwable ->
        future.complete(if (throwable != null) Failure(throwable) else Success(result))
    }
    return future
}

fun <T> Future<Try<T>>.dematerialize(): Future<T> {
    val future = Future<T>()
    this.thenApply {
        when (it) {
            is Success<T> -> future.complete(it.value)
            is Failure<*> -> future.completeExceptionally(it.error)
        }
    }
    return future
}

fun <A, B> Future<A>.map(f: (A) -> B): Future<B> = thenApply(f)

fun <A, B> Future<A>.flatMap(f: (A) -> Future<B>): Future<B> = thenCompose(f)

fun <T> Future<T>.void(): Future<Unit> = map { Unit }

fun <A> Future<A>.withErrorMessage(message: String): Future<A> {
    val future = Future<A>()
    handle { result, throwable ->
        if (throwable != null) future.completeExceptionally(RuntimeException(message, throwable))
        else future.complete(result)
    }
    return future
}

fun <A> Future<A>.onError(f: (Throwable) -> Unit): Future<A> =
        apply {
            handle { _, throwable -> if (throwable != null) f(throwable) }
        }

suspend fun <A> Future<A>.await(): Try<A> =
        suspendCoroutine { cont ->
            handle { result, throwable ->
                cont.resume(if (throwable != null) Failure(throwable) else Success(result))
            }
        }