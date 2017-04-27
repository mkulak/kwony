package com.xap4o.kwony.http

import com.xap4o.kwony.utils.basicAuthHeader
import com.xap4o.kwony.utils.oauth2Header
import com.xap4o.kwony.utils.toMultiMap
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import java.net.URL
import java.time.Duration
import java.util.concurrent.CompletableFuture


data class HttpRequest(
        val url: String,
        val method: HttpMethod = HttpMethod.GET,
        val params: Map<String, String> = emptyMap(),
        val body: HttpEntity = Empty,
        val headers: Map<String, String> = emptyMap(),
        val timeout: Duration = Duration.ofSeconds(10)) {

    fun withMethod(method: HttpMethod) = copy(method = method)

    fun withBody(body: HttpEntity) = copy(body = body)

    fun withParams(params: Map<String, String>) = copy(params = params)

    fun withHeader(header: Pair<String, String>) = copy(headers = headers + header)

    fun withBasicAuth(name: String, password: String) = withHeader(basicAuthHeader(name, password))

    fun withTimeout(timeout: Duration) = copy(timeout = timeout)

    fun withOAuth2(token: String) = withHeader(oauth2Header(token))
}

sealed class HttpEntity
data class Form(val params: Map<String, String>) : HttpEntity()
data class Json(val payload: Any) : HttpEntity()
object Empty : HttpEntity() {
    override fun toString() = "Empty"
}


interface HttpClient {
    fun execute(req: HttpRequest): CompletableFuture<HttpResponse<Buffer>>
}

class HttpClientImpl(vertx: Vertx) : HttpClient {
    val webClient = WebClient.create(vertx)
    val httpsWebClient = WebClient.create(vertx, WebClientOptions().setSsl(true))

    override fun execute(req: HttpRequest): CompletableFuture<HttpResponse<Buffer>> {
        val future = CompletableFuture<HttpResponse<Buffer>>()
        fun handler(res: AsyncResult<HttpResponse<Buffer>>) {
            if (res.succeeded()) future.complete(res.result()) else future.completeExceptionally(res.cause())
        }
        val url = URL(req.url)
        val port = if (url.port == -1) url.defaultPort else url.port
        val client = if (url.protocol == "https") httpsWebClient else webClient
        val vertxReq = client.request(req.method, port, url.host, url.path)
//        vertxReq.ssl(url.protocol == "https")
        vertxReq.timeout(req.timeout.toMillis())
        req.params.forEach { (name, value) -> vertxReq.addQueryParam(name, value) }
        req.headers.forEach { (name, value) -> vertxReq.putHeader(name, value) }
        when (req.body) {
            is Empty -> vertxReq.send(::handler)
            is Json -> vertxReq.sendJson(req.body.payload, ::handler)
            is Form -> vertxReq.sendForm(req.body.params.toMultiMap(), ::handler)
        }
        return future
    }
}

