import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.coroutines.experimental.suspendCoroutine

fun main(args: Array<String>) {
    myCor {
        val i: Int = readInt()
        val s: String = readString()
        println("$s - $i")
    }
    sleep(5000)
}

fun myCor(f: suspend Foo.() -> Unit) {
    val foo = Foo()
    f.startCoroutine(foo, foo)
}

class Foo : Continuation<Any> {

    suspend fun readInt(): Int {
        return suspendCoroutine<Int> { c ->
            thread {
                sleep(1000)
                println("async read int")
                c.resume(1)
            }
        }
    }
    
    suspend fun readString(): String {
        return suspendCoroutine<String> { c ->
            thread {
                sleep(1000)
                println("async read string")
                c.resume("foo")
            }
        }
    }


    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resume(value: Any) {
        println("resume with $value")
    }

    override fun resumeWithException(exception: Throwable) {
        println("resume with $exception")
    }
}
