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
import com.xap4o.kwony.twitter.Token
import com.xap4o.kwony.twitter.TwitterClientImpl
import com.xap4o.kwony.utils.Timer
import com.xap4o.kwony.utils.map
import com.xap4o.kwony.utils.materialize
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import java.util.concurrent.ScheduledThreadPoolExecutor

fun main(args: Array<String>): Unit {
    Json.mapper.registerKotlinModule()

    val config = loadConfig()
    Db.migrate(config.db)

    val db = Db.initSessionFactory(config.db)
    val resultsDb = AnalyzeResultDb(db)
    val keywordsDb = SearchKeywordsDb(db)
    val pool = ScheduledThreadPoolExecutor(1)

    val vertx = Vertx.vertx()
    val httpClient = HttpClientImpl(WebClient.create(vertx))
    val twitterClient = TwitterClientImpl(config.processing, httpClient)
    val analyzerClient = AnalyzerClientImpl(config.processing, httpClient)
    val job = AnalyzeJob(twitterClient, analyzerClient, Timer.system)
    val keywordsServer = KeywordsServer(keywordsDb)

    val router = Router.router(vertx)
    AnalyzerServer.api(router)
    keywordsServer.api(router)

//    PeriodicProcessing(job, config.processing, resultsDb, keywordsDb, pool).start()

    println("starting http server on http://${config.http.host}:${config.http.port}")
    val token = Token("")
    twitterClient.search(token, "trump").materialize().map { println(it) }
//    vertx.createHttpServer().requestHandler(router::accept).listen(config.http.port, config.http.host)
}


