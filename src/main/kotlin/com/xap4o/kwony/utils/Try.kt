package com.xap4o.kwony.utils


sealed class Try<T> {
    abstract fun isSuccess(): Boolean

    fun onError(f: (Throwable) -> Unit) = if (this is Failure) f(error) else Unit

    companion object {
        operator fun <T> invoke(f: () -> T): Try<T> = try {
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