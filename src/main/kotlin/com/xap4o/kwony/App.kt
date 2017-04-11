package com.xap4o.kwony

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
import io.vertx.ext.web.Router

fun main(args: Array<String>): Unit {
    val config = loadConfig()
    Db.migrate(config.db)

    val db = Db.initSessionFactory(config.db)
    val resultsDb = AnalyzeResultDb(db)
    val keywordsDb = SearchKeywordsDb(db)
    val httpClient = HttpClientImpl()
    val twitterClient = TwitterClientImpl(config.processing, httpClient)
    val analyzerClient = AnalyzerClientImpl(config.processing, httpClient)
    val job = AnalyzeJob(twitterClient, analyzerClient, Timer.system)
    val keywordsServer = KeywordsServer(keywordsDb)

    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    AnalyzerServer.api(router)
    keywordsServer.api(router)

    PeriodicProcessing(job, config.processing, resultsDb, keywordsDb).start()
    vertx.createHttpServer().requestHandler(router::accept).listen(config.http.port)
}


