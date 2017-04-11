package com.xap4o.kwony.http

import com.xap4o.kwony.db.SearchKeywordsDb
import com.xap4o.kwony.utils.endWithJson
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router

class KeywordsServer(val keywordsDb: SearchKeywordsDb) {
    fun api(router: Router): Router =
            router.apply {
                route(HttpMethod.GET, "/search_keywords").handler { context ->
                    context.response().endWithJson(keywordsDb.getAll())
                }
                route(HttpMethod.POST, "/search_keywords").handler { context ->
                    val keyword = context.request().getParam("keyword")
                    keywordsDb.persist(keyword)
                    context.response().endWithJson("done")
                }
                route(HttpMethod.DELETE, "/search_keywords").handler { context ->
                    val keyword = context.request().getParam("keyword")
                    keywordsDb.delete(keyword)
                    context.response().endWithJson("done")
                }
            }
}


