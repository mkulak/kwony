package com.xap4o.kwony.http

import io.vertx.core.Vertx
import io.vertx.ext.web.Router

open class HttpApi(val routes: Router.() -> Unit) {
    fun api(vertx: Vertx): Router = Router.router(vertx).apply { routes() }
}
