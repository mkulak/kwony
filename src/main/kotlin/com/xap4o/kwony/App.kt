package com.xap4o.kwony

import com.xap4o.kwony.config.HttpConfig
import com.xap4o.kwony.config.loadConfig
import com.xap4o.kwony.db.AnalyzeResultDb
import com.xap4o.kwony.db.Db
import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpClientImpl
import com.xap4o.kwony.processing.AnalyzeJob
import com.xap4o.kwony.processing.AnalyzeResult
import com.xap4o.kwony.processing.AnalyzerClientImpl
import com.xap4o.kwony.processing.PeriodicProcessing
import com.xap4o.kwony.twitter.TwitterClientImpl
import com.xap4o.kwony.utils.Timer
import javax.sql.DataSource

fun main(args: Array<String>): Unit {
    val config = loadConfig()
    Db.migrate(config.db)
    val db = Db.initSessionFactory(config.db)
    val resultsDb = AnalyzeResultDb(db)
    val keywordsDb = SearchKeywordsDb(db)
//    val httpClient = HttpClientImpl()
//    val twitterClient = TwitterClientImpl(config.processing, httpClient)
//    val analyzerClient = AnalyzerClientImpl(config.processing, httpClient)
//
//    val job = AnalyzeJob(twitterClient, analyzerClient, Timer.system)
//
//    PeriodicProcessing(job, config.processing, resultsDb, keywordsDb).start()

//    val keywordsServer = KeywordsServer(keywordsDb)
//    startServerAndBlock(config.http, AnalizerServer.route ~ keywordsServer.route)
}

//private fun startServerAndBlock(config: HttpConfig, route: Route): Unit {
//    val future: Future[ServerBinding] = Http().bindAndHandle(route, config.host, config.port)
//    future.foreach {s => LOG.info(s"Server started at http://${config.host}:${config.port}") }
//    LOG.info("Press Enter to terminate")
//    StdIn.readLine()
//    future.flatMap(_.unbind()).onComplete(_ => implicitly[ActorSystem].terminate())
//}


