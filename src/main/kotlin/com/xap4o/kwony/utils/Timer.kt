package com.xap4o.kwony.utils

interface Clock {
    fun now(): Long
}

object SystemClock : Clock {
    override fun now(): Long = System.currentTimeMillis()
}

typealias TimerFactory = () -> Timer

class Timer(val clock: Clock) {
    private val startTime: Long = clock.now()

    fun elapsed() = clock.now() - startTime
}
