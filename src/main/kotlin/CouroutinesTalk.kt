import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.createCoroutine
import kotlin.coroutines.experimental.suspendCoroutine

fun main(args: Array<String>) {
    val g = generate {
        for (i in 0..10) {
            for (j in -1..1) {
                yield(i + j)
            }
            yield(-i * 10)
        }
    }
    repeat(10) {
        println(g.next())
    }
}


fun generate(block: suspend GenImpl.() -> Unit): Gen {
    return GenImpl().apply {
        cont = block.createCoroutine(completion = this, receiver = this)
    }

}

interface Gen {
    fun next(): Int
}

class GenImpl : Gen, Continuation<Unit> {
    override val context: CoroutineContext = EmptyCoroutineContext

    lateinit var cont: Continuation<Unit>

    var curValue: Int = 0

    override fun resume(value: Unit) {
        println("resume")
    }

    override fun resumeWithException(exception: Throwable) = throw exception

    override fun next(): Int {
        cont.resume(Unit)
        return curValue
    }

    suspend fun yield(i: Int): Unit {
        curValue = i
        suspendCoroutine<Unit> { c -> cont = c }
    }
}



