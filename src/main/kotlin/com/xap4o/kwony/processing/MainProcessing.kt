package com.xap4o.kwony.processing

import com.xap4o.kwony.db.AnalyzeResultDb
import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.utils.Failure
import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Success
import com.xap4o.kwony.utils.Try
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch


class MainProcessing(
        val job: AnalyzeJob,
        val resultDb: AnalyzeResultDb,
        val keywordsDb: SearchKeywordsDb) : Logging {

    fun process(): Unit {
        launch(CommonPool) {
            val results = keywordsDb.getAll().orDie().map { keyword -> job.process(keyword) }
            handleResults(results)
        }
    }

    private fun handleResults(results: List<Try<AnalyzeResult>>) {
        results.forEach {
            when (it) {
                is Success -> {
                    LOG.info(it.value.toString())
                    resultDb.persist(it.value).orDie()
                }
                is Failure ->
                    LOG.error("Error while processing:", it.error)
            }
        }
    }
}
