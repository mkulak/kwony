package com.xap4o.kwony.utils

import java.util.concurrent.CompletableFuture

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

fun <A> CompletableFuture<A>.onError(f: (Throwable) -> Unit): CompletableFuture<A> =
    apply {
        handle { _, throwable -> if (throwable != null) f(throwable) }
    }