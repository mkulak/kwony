package com.xap4o.kwony.processing

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.db.AnalyzeResultDb
import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.utils.Logging
import org.apache.logging.log4j.core.util.Cancellable


class PeriodicProcessing(
        job: AnalyzeJob,
        config: ProcessingConfig,
        resultDb: AnalyzeResultDb,
        keywordsDb: SearchKeywordsDb) : Logging {

    fun start() {
//    val task = new Runnable { override def run(): Unit = process()}
//    implicitly[ActorSystem].scheduler.schedule(0.seconds, config.interval, task)
    }

    fun process(): Unit {
//    Source.fromFuture(keywordsDb.getAll()).
//      .rightFlatMap { keywords =>
//        keywords.map(k => job.process(k))
//      }
//      .evalMap {
//        case Success(res) =>
//          LOG.info(res.toString)
//          resultDb.persist(res).map(Success.apply)
//        case Failure(error) =>
//          LOG.error("Error while processing:", error)
//          Task.now(Failure(error))
//      }
//      .run
    }
}
