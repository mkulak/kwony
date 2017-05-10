package com.xap4o.kwony

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.xap4o.kwony.config.loadConfig
import com.xap4o.kwony.db.Db
import com.xap4o.kwony.db.PostgresAnalyzeResultDb
import com.xap4o.kwony.db.PostgresSearchKeywordsDb
import com.xap4o.kwony.http.AnalyzerServer
import com.xap4o.kwony.http.HttpClientImpl
import com.xap4o.kwony.http.KeywordsServer
import com.xap4o.kwony.processing.AnalyzeJobImpl
import com.xap4o.kwony.processing.AnalyzerClientImpl
import com.xap4o.kwony.processing.MainProcessing
import com.xap4o.kwony.processing.Scheduling
import com.xap4o.kwony.twitter.TwitterClientImpl
import com.xap4o.kwony.utils.SystemClock
import com.xap4o.kwony.utils.Timer
import com.xap4o.kwony.utils.plus
import com.xap4o.kwony.utils.start
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import org.slf4j.LoggerFactory

val LOG = LoggerFactory.getLogger("App")

fun main(args: Array<String>): Unit {
    Json.mapper.registerKotlinModule()

    val config = loadConfig()

    val db = Db.init(config.db)
    val resultsDb = PostgresAnalyzeResultDb(db)
    val keywordsDb = PostgresSearchKeywordsDb(db)

    val vertx = Vertx.vertx()
    val httpClient = HttpClientImpl(vertx)
    val twitterClient = TwitterClientImpl(config.twitter, httpClient)
    val analyzerClient = AnalyzerClientImpl(config.analyze, httpClient)
    val job = AnalyzeJobImpl(twitterClient, analyzerClient, { Timer(SystemClock) })
    val mainProcessing = MainProcessing(job, resultsDb, keywordsDb)

    val httpApi = AnalyzerServer.api(vertx) + KeywordsServer(keywordsDb).api(vertx)

    Scheduling.schedule(config.processing.interval, mainProcessing::process)

    LOG.info("starting http server on ${config.http.host}")
    vertx.createHttpServer().start(httpApi, config.http.host)
}
