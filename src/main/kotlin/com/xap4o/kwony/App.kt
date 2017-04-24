package com.xap4o.kwony

import com.xap4o.kwony.config.loadConfig
import com.xap4o.kwony.db.AnalyzeResultDb
import com.xap4o.kwony.db.Db
import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.http.AnalyzerServer
import com.xap4o.kwony.http.Form
import com.xap4o.kwony.http.HttpClientImpl
import com.xap4o.kwony.http.HttpRequest
import com.xap4o.kwony.http.KeywordsServer
import com.xap4o.kwony.processing.AnalyzeJob
import com.xap4o.kwony.processing.AnalyzerClientImpl
import com.xap4o.kwony.twitter.AuthResponse
import com.xap4o.kwony.twitter.TwitterClientImpl
import com.xap4o.kwony.utils.Failure
import com.xap4o.kwony.utils.Timer
import com.xap4o.kwony.utils.map
import com.xap4o.kwony.utils.materialize
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import java.util.concurrent.ScheduledThreadPoolExecutor

fun main(args: Array<String>): Unit {
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


//    launch(CommonPool) {
//        delay(1000)
//        val f = httpClient.make(HttpRequest("http://localhost:8080/search_keywords", HttpMethod.GET), List::class.java)
//        f.compose {
//            httpClient.make(HttpRequest("http://localhost:8080/search_keywords", HttpMethod.GET), List::class.java)
//        }.map {
//            println(it)
//        }
//    }
    println("starting")

    val req = HttpRequest("${config.processing.twitterHost}/oauth2/token", HttpMethod.POST)
            .withBody(Form(mapOf("grant_type" to "client_credentials")))
            .withTimeout(config.processing.timeout)
            .withBasicAuth(config.processing.twitterKey, config.processing.twitterSecret)
    httpClient.make(req, AuthResponse::class.java).materialize().map {
        println("lalala")
        if (it is Failure<*>) it.error.printStackTrace() else println(it)
    }
//    PeriodicProcessing(job, config.processing, resultsDb, keywordsDb, pool).start()
//    vertx.createHttpServer().requestHandler(router::accept).listen(config.http.port)

}


