package com.xap4o.kwony.utils

typealias CreateTimer = () -> () -> Long

object Timer {
    fun now(): Long = System.currentTimeMillis()

    val system: CreateTimer = run {
        val start = now()
        val r = { now() - start }
        { r }
    }
}
