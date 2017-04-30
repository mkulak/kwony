package com.xap4o.kwony.processing

import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Try
import java.time.Duration
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

object Scheduling : Logging {
    val pool = ScheduledThreadPoolExecutor(1)

    fun schedule(interval: Duration, job: () -> Unit) {
        val wrappedJob = {
            Try.catchNonFatal {
                job()
            }.onError {
                LOG.error("Unexpected exception: ", it)
            }
        }
        pool.scheduleAtFixedRate(wrappedJob, 0, interval.toMillis(), TimeUnit.MILLISECONDS)
    }
}
