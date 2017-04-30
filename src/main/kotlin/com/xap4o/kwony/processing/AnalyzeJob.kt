package com.xap4o.kwony.processing

import com.xap4o.kwony.twitter.Keyword
import com.xap4o.kwony.twitter.TwitterClient
import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Success
import com.xap4o.kwony.utils.TimerFactory
import com.xap4o.kwony.utils.Try


interface AnalyzeJob {
    suspend fun process(keyword: Keyword): Try<AnalyzeResult>
}

class AnalyzeJobImpl(
        val twitterClient: TwitterClient,
        val analyzerClient: AnalyzerClient,
        val timerFactory: TimerFactory) : AnalyzeJob, Logging {

    override suspend fun process(keyword: Keyword): Try<AnalyzeResult> =
        Try {
            val timer = timerFactory()
            val token = twitterClient.open().orDie()
            val searchResponse = twitterClient.search(token, keyword).orDie()
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