import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.createCoroutine
import kotlin.coroutines.experimental.suspendCoroutine

fun main(args: Array<String>) {
    println("start")
    val g = gen {
        for (i in 0..10) {
            if (i % 3 == 0) {
                for (j in -1..1) {
                    yield(i + j)
                }
                yield(i * 10)
            }
            yield(-i)
        }
    }
    repeat(120) {
        println(g.next())
    }
}


fun gen(block: suspend GenImpl.() -> Unit): Gen {
    return GenImpl().apply {
        cont = block.createCoroutine(completion = this, receiver = this)
    }

}

interface Gen {
    fun next(): Int
}

class GenImpl : Gen, Continuation<Unit> {
    lateinit var cont: Continuation<Unit>

    var curValue: Int = 0

    override fun resume(value: Unit) {
        println("resume")
    }

    override fun resumeWithException(exception: Throwable) = throw exception

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun next(): Int {
        cont.resume(Unit)
        return curValue
    }

    suspend fun yield(i: Int): Unit {
        curValue = i
        suspendCoroutine<Unit> { c -> cont = c }
    }
}

//what is stack, frame lifecycle
//how coroutines allows to "step out" of the frame saving it's state and then resume later.
//interface Continuation<T> { //simplified for bravity
//    fun resume(value: T)
//    fun resumeWithException(exception: Throwable)
//}
//coroutines are not necessarily async!

//example generagtor in python
//simple generator in Kotlin without coroutines
//adding coroutines

//bonus: make it extend iterator
//bonus: make lazy tree traversal?
//bonus: talk about continuing coroutine execution with result (compiler magic!)



