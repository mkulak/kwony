package com.xap4o.kwony.utils


sealed class Try<T> {
    abstract fun isSuccess(): Boolean

    fun onError(f: (Throwable) -> Unit) = if (this is Failure) f(error) else Unit

    fun orDie() = when(this) {
            is Success -> value
            is Failure -> throw error
        }

    companion object {
        suspend operator fun <T> invoke(f: suspend () -> T): Try<T> = try {
            Success(f())
        } catch (e: Throwable) {
            Failure(e)
        }
    }
}

data class Success<T>(val value: T) : Try<T>() {
    override fun isSuccess() = true
}

data class Failure<T>(val error: Throwable) : Try<T>() {
    override fun isSuccess() = false
}