package com.xap4o.kwony.processing

import com.xap4o.kwony.twitter.TwitterClient
import com.xap4o.kwony.utils.CreateTimer
import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Success
import com.xap4o.kwony.utils.partitionAs


class AnalyzeJob(
        val twitterClient: TwitterClient,
        val analyzerClient: AnalyzerClient,
        val createTimer: CreateTimer) : Logging {

    suspend fun process(query: String): AnalyzeResult {
        val timer = createTimer()
        val token = twitterClient.open().orDie
        val searchResponse = twitterClient.search(token, query).orDie
        val results = searchResponse.tweets.map { analyzerClient.analyze(it) }
        val (success, failures) = results.partitionAs<Success<Boolean>>()
        val positiveCount = success.count { it.value }
        val negativeCount = success.size - positiveCount
        val errorsCount = results.size - failures.size
        val duration = timer()
        val realQuery = searchResponse.metadata.query
        return AnalyzeResult(realQuery, results.size, positiveCount, negativeCount, errorsCount, duration)
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