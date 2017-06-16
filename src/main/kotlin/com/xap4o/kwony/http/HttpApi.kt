package com.xap4o.kwony.http

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import java.net.URL

open class HttpApi(initFunc: HttpApi.() -> Unit) {
    val routes = arrayListOf<Route>()

    init {
        initFunc()
    }

    fun route(method: HttpMethod, path: String, handler: Handler) = routes.add(Route(method, path, handler))

    fun api(vertx: Vertx): Router {
        val router: Router = Router.router(vertx)
        routes.forEach {
            router.route(it.method, it.path).handler(BodyHandler.create())
            router.route(it.method, it.path).handler { context ->
                val vxReq = context.request()
                val vxResp = context.response()
                val request = HttpRequest(URL(vxReq.absoluteURI()))
                        .withMethod(vxReq.method())
                        .withParams(vxReq.params().toNormalMap())
                        .withBody(BinaryBody(context.body))
                val response = it.handler(request)
                vxResp.statusCode = response.statusCode
                response.headers.forEach {
                    vxResp.putHeader(it.key, it.value)
                }
                vxResp.end(response.body)
            }
        }
        return router
    }
}





