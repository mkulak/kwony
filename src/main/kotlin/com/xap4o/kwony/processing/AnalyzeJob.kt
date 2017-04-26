package com.xap4o.kwony.processing

import com.xap4o.kwony.twitter.SearchResponse
import com.xap4o.kwony.twitter.TwitterClient
import com.xap4o.kwony.utils.CreateTimer
import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Success
import com.xap4o.kwony.utils.flatMap
import com.xap4o.kwony.utils.gatherUnordered
import com.xap4o.kwony.utils.map
import com.xap4o.kwony.utils.materialize
import java.util.concurrent.CompletableFuture


class AnalyzeJob(
        val twitterClient: TwitterClient,
        val analyzerClient: AnalyzerClient,
        val createTimer: CreateTimer) : Logging {

    fun process(query: String): CompletableFuture<AnalyzeResult> {
        val timer = createTimer()
        return twitterClient
                .open()
                .flatMap { twitterClient.search(it, query) }
                .flatMap { searchResult: SearchResponse ->
                    searchResult.tweets.map(analyzerClient::analyze).map { it.materialize() }.gatherUnordered().map { results ->
                        val (success, failures) = results.partition { it.isSuccess() }
                        val positiveCount = success.count { (it as Success<Boolean>).value }
                        val negativeCount = success.size - positiveCount
                        val errorsCount = results.size - failures.size
                        val duration = timer()
                        val realQuery = searchResult.metadata.query
                        AnalyzeResult(realQuery, results.size, positiveCount, negativeCount, errorsCount, duration)
                    }
                }
    }
}

data class AnalyzeResult(
        val query: String,
        val total: Int,
        val positive: Int,
        val negative: Int,
        val errors: Int,
        val duration: Long
)