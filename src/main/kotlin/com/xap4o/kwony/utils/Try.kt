package com.xap4o.kwony.utils


sealed class Try<A> {
    abstract fun isSuccess(): Boolean

    fun onError(f: (Throwable) -> Unit) = if (this is Failure) f(error) else Unit

    fun orDie() = when (this) {
        is Success -> value
        is Failure -> throw error
    }

    fun <B> map(f: (A) -> B): Try<B> = when (this) {
        is Success -> Success(f(value))
        is Failure -> Failure(error)
    }

    fun filter(p: (A) -> Boolean) = when (this) {
        is Success -> if (p(value)) this else Failure(NoSuchElementException("Predicate does not hold for $value"))
        is Failure -> this
    }

    fun <B> flatMap(f: (A) -> Try<B>): Try<B> = when (this) {
        is Success -> f(value)
        is Failure -> Failure(error)
    }

    fun withErrorMessage(message: String): Try<A> = when (this) {
        is Success -> this
        is Failure -> Failure(RuntimeException(message, error))
    }

    companion object {
        suspend operator fun <T> invoke(f: suspend () -> T): Try<T> = try {
            Success(f())
        } catch (e: Throwable) {
            Failure(e)
        }

        fun <T> catchNonFatal(f: () -> T): Try<T> = try {
            Success(f())
        } catch (e: Throwable) {
            Failure(e)
        }
    }
}

data class Success<A>(val value: A) : Try<A>() {
    override fun isSuccess() = true
}

data class Failure<A>(val error: Throwable) : Try<A>() {
    override fun isSuccess() = false
}

fun Throwable.prettyPrint(): String {
    fun unwind(t: Throwable?, acc: List<Throwable>): List<Throwable> = if (t == null) acc else unwind(t.cause, acc + t)
    return unwind(this, arrayListOf()).map { "${it.javaClass.simpleName}: ${it.message}"}.joinToString(" <- ")
}