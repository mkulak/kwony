package com.xap4o.kwony.utils

import io.vertx.core.MultiMap
import io.vertx.core.http.*
import io.vertx.core.json.Json
import io.vertx.ext.web.*
import java.net.URL
import java.util.Base64

fun HttpServer.start(router: Router, url: URL) = requestHandler(router::accept).listen(url.port, url.host)

fun HttpServerResponse.endWithJson(obj: Any) {
    putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
}

fun Map<String, String>.toMultiMap(): MultiMap = MultiMap.caseInsensitiveMultiMap().apply { addAll(this@toMultiMap) }

fun String.encodeBase64(): String =
        Base64.getEncoder().encode(this.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)

fun basicAuthHeader(name: String, password: String): Pair<String, String> =
        "Authorization" to ("Basic " + ("$name:$password".encodeBase64()))

fun oauth2Header(token: String): Pair<String, String> =
        "Authorization" to "Bearer $token"

fun URL.withPath(path: String): URL = URL("$this$path")

data class Scopes(val values: List<String>)

fun protect(vararg scopes: String) = Scopes(scopes.toList())

typealias Handler = (RoutingContext, HttpServerRequest, HttpServerResponse) -> Unit

fun Router.get(path: String, handler: Handler): Route = route(HttpMethod.GET, path, null, handler)
fun Router.post(path: String, handler: Handler): Route = route(HttpMethod.POST, path, null, handler)
fun Router.get(path: String, scopes: Scopes?, handler: Handler): Route = route(HttpMethod.GET, path, scopes, handler)
fun Router.post(path: String, scopes: Scopes?, handler: Handler): Route = route(HttpMethod.POST, path, scopes, handler)

fun Router.route(method: HttpMethod, path: String, scopes: Scopes?, handler: Handler): Route {
    val route = route(method, path)
    val protectedRoute = if (scopes != null) route.handler(protect(scopes)) else route
    return protectedRoute.handler { ctx -> handler(ctx, ctx.request(), ctx.response()) }
}

fun protect(scopes: Scopes) = { ctx: RoutingContext ->
    val token = ctx.request().getHeader("Authorization")
    if (token == null) ctx.response().setStatusCode(401).end("No auth token")
    if (checkScope(token, scopes.values)) ctx.next() else ctx.response().setStatusCode(403).end("Not authorized")
}

fun checkScope(token: String, scopes: List<String>) = true




