package com.xap4o.kwony.http

import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.twitter.Keyword
import com.xap4o.kwony.utils.endWithJson
import io.vertx.core.http.HttpMethod

class KeywordsServer(val keywordsDb: SearchKeywordsDb) : HttpApi({
    route(HttpMethod.GET, "/search_keywords").handler { context ->
        context.response().endWithJson(keywordsDb.getAll().orDie())
    }
    route(HttpMethod.POST, "/search_keywords").handler { context ->
        val keyword = context.request().getParam("keyword")
        keywordsDb.persist(Keyword(keyword)).orDie()
        context.response().endWithJson("done")
    }
    route(HttpMethod.DELETE, "/search_keywords").handler { context ->
        val keyword = context.request().getParam("keyword")
        keywordsDb.delete(Keyword(keyword)).orDie()
        context.response().endWithJson("done")
    }
})


