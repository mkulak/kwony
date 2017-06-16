package com.xap4o.kwony.http

import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.twitter.Keyword
import io.vertx.core.http.HttpMethod

class KeywordsServer(val keywordsDb: SearchKeywordsDb) : HttpApi2({
    route(HttpMethod.GET, "/search_keywords") { request ->
        jsonResponse(keywordsDb.getAll().orDie())
    }
    route(HttpMethod.POST, "/search_keywords") { request ->
        val keyword = request.params["keyword"]!!
        keywordsDb.persist(Keyword(keyword)).orDie()
        jsonResponse("done")
    }
    route(HttpMethod.DELETE, "/search_keywords") { request ->
        val keyword = request.params["keyword"]!!
        keywordsDb.delete(Keyword(keyword)).orDie()
        jsonResponse("done")
    }
})
