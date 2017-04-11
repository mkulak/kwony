package com.xap4o.kwony.processing

import com.xap4o.kwony.twitter.TwitterClient
import com.xap4o.kwony.utils.CreateTimer
import com.xap4o.kwony.utils.Logging
import java.util.concurrent.Future


class AnalyzeJob(
        twitterClient: TwitterClient,
        analyzerClient: AnalyzerClient,
        createTimer: CreateTimer) : Logging {

  fun process(query: String): Future<AnalyzeResult> {
//    val timer = createTimer()
//    twitterClient
//      .open()
//      .rightFlatMap(token => twitterClient.search(token, query))
//      .rightFlatMap { searchResult =>
//        Future.sequence(searchResult.tweets.map(analyzerClient.analyze)).map { results =>
//          val success = results.collect { case Success(result) => result }
//          val positiveCount = success.count(identity)
//          val negativeCount = success.size - positiveCount
//          val errorsCount = results.size - success.size
//          val duration = timer()
//          val realQuery = searchResult.metadata.query
//          Success(AnalyzeResult(realQuery, results.size, positiveCount, negativeCount, errorsCount, duration))
//        }
//      }
      TODO()
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