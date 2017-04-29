package com.xap4o.kwony

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.xap4o.kwony.config.loadConfig
import com.xap4o.kwony.db.AnalyzeResultDb
import com.xap4o.kwony.db.Db
import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.http.AnalyzerServer
import com.xap4o.kwony.http.HttpClientImpl
import com.xap4o.kwony.http.KeywordsServer
import com.xap4o.kwony.processing.AnalyzeJob
import com.xap4o.kwony.processing.AnalyzerClientImpl
import com.xap4o.kwony.processing.PeriodicProcessing
import com.xap4o.kwony.twitter.TwitterClientImpl
import com.xap4o.kwony.utils.Timer
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledThreadPoolExecutor

val LOG = LoggerFactory.getLogger("App")

fun main(args: Array<String>): Unit {
    Json.mapper.registerKotlinModule()

    val config = loadConfig()
    Db.migrate(config.db)

    val db = Db.initSessionFactory(config.db)
    val resultsDb = AnalyzeResultDb(db)
    val keywordsDb = SearchKeywordsDb(db)
    val pool = ScheduledThreadPoolExecutor(1)

    val vertx = Vertx.vertx()
    val httpClient = HttpClientImpl(vertx)
    val twitterClient = TwitterClientImpl(config.processing, httpClient)
    val analyzerClient = AnalyzerClientImpl(config.processing, httpClient)
    val job = AnalyzeJob(twitterClient, analyzerClient, Timer.system)

    val router = Router.router(vertx)
    AnalyzerServer.api(router)
    KeywordsServer(keywordsDb).api(router)

    PeriodicProcessing(job, config.processing, resultsDb, keywordsDb, pool).start()

    LOG.info("starting http server on http://${config.http.host}:${config.http.port}")
    vertx.createHttpServer().requestHandler(router::accept).listen(config.http.port, config.http.host)
//    val token = Token("")
//    twitterClient.search(token, "trump").materialize().map { println(it) }
}


