package com.xap4o.kwony.processing

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.db.AnalyzeResultDb
import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.utils.Logging
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


class PeriodicProcessing(
        val job: AnalyzeJob,
        val config: ProcessingConfig,
        val resultDb: AnalyzeResultDb,
        val keywordsDb: SearchKeywordsDb,
        val pool: ScheduledThreadPoolExecutor) : Logging {

    fun start(): ScheduledFuture<*> =
            pool.scheduleAtFixedRate(this::process, 0, config.interval.toMillis(), TimeUnit.MILLISECONDS)

    fun process(): Unit {
        keywordsDb.getAll().map { keyword ->
            job.process(keyword).setHandler { result ->  //TODO: beautify
                if (result.succeeded()) {
                    val value = result.result()
                    LOG.info(value.toString())
                    resultDb.persist(value)
                } else {
                    LOG.error("Error while processing:", result.cause())
                }
            }
        }
    }
}
