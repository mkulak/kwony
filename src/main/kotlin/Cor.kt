@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.createCoroutine
import kotlin.coroutines.experimental.startCoroutine
import kotlin.coroutines.experimental.suspendCoroutine

suspend fun <T> await(f: CompletableFuture<T>): T =
        suspendCoroutine<T> { c: Continuation<T> ->
            f.whenComplete { result, exception ->
                if (exception == null) // the future has been completed normally
                    c.resume(result)
                else // the future has completed with an exception
                    c.resumeWithException(exception)
            }
        }

suspend fun File.aRead(): ByteArray {
    val channel = AsynchronousFileChannel.open(toPath());
    val tentativeSize = channel.size()
    if (tentativeSize > Int.MAX_VALUE) throw IOException("File is too large to read into byte array")
    val buffer = ByteBuffer.allocate(tentativeSize.toInt())
    return suspendCoroutine { c ->
        channel.read(buffer, 0L, Unit, object : CompletionHandler<Int, Unit> {
            override fun completed(bytesRead: Int, attachment: Unit) {
                val n = bytesRead.coerceAtLeast(0)
                val bytes = if (n == buffer.capacity()) buffer.array()
                else buffer.array().copyOf(n)
                c.resume(bytes)
            }

            override fun failed(exception: Throwable, attachment: Unit) {
                c.resumeWithException(exception)
            }
        })
    }
}

interface AsyncIterator<out T> {

    suspend operator fun hasNext(): Boolean
    suspend operator fun next(): T
}

suspend fun aFoo(i: String): String {
    println("inside aFoo $i")
    return suspendCoroutine { c ->
        println("inside suspend")
        val line = readLine()
        c.resume("$line: $i")
    }
}

fun <T> async(block: suspend () -> T): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    block.startCoroutine(completion = object : Continuation<T> {
        override fun resume(value: T) {
            future.complete(value)
        }
        override fun resumeWithException(exception: Throwable) {
            future.completeExceptionally(exception)
        }

        override val context: CoroutineContext
            get() = EmptyCoroutineContext
    })
    return future
}
//    async {
//        val r = aFoo("s1")
//        println("got result from aFoo: $r")
//        val r2 = aFoo("s2")
//        println("got result from second aFoo call: $r2")
//        r2
//    }.handle { result, error ->  println("get result: $result  and error: $error")}

