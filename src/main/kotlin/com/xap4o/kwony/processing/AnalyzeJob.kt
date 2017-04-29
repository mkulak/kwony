package com.xap4o.kwony.processing

import com.xap4o.kwony.twitter.TwitterClient
import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Success
import com.xap4o.kwony.utils.TimerFactory
import com.xap4o.kwony.utils.Try


class AnalyzeJob(
        val twitterClient: TwitterClient,
        val analyzerClient: AnalyzerClient,
        val timerFactory: TimerFactory) : Logging {

    suspend fun process(query: String): Try<AnalyzeResult> =
        Try {
            val timer = timerFactory()
            val token = twitterClient.open().orDie()
            val searchResponse = twitterClient.search(token, query).orDie()
            val results = searchResponse.tweets.map { analyzerClient.analyze(it) }
            val success = results.filterIsInstance<Success<Boolean>>()
            val positiveCount = success.count { it.value }
            val negativeCount = success.size - positiveCount
            val errorsCount = results.size - success.size
            val duration = timer.elapsed()
            val realQuery = searchResponse.metadata.query
            AnalyzeResult(realQuery, results.size, positiveCount, negativeCount, errorsCount, duration)
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