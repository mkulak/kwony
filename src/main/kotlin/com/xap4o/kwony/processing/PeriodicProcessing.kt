package com.xap4o.kwony.processing

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.db.AnalyzeResultDb
import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.utils.Failure
import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Success
import com.xap4o.kwony.utils.Try
import com.xap4o.kwony.utils.gatherUnordered
import com.xap4o.kwony.utils.map
import com.xap4o.kwony.utils.materialize
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

    private fun process(): Unit {
        Try {
            keywordsDb.getAll().map { keyword ->
                LOG.info("process $keyword")
                job.process(keyword).materialize()
            }.gatherUnordered().map(this::handleResults)
        }.onError {
            LOG.error("Error while processing: ", it)
        }
    }

    private fun handleResults(results: List<Try<AnalyzeResult>>) {
        results.forEach {
            when(it) {
                is Success -> {
                    LOG.info(it.value.toString())
                    resultDb.persist(it.value)
                }
                is Failure ->
                    LOG.error("Error while processing:", it.error)
            }
        }
    }
}
