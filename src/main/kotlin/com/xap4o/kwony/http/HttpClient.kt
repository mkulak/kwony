package com.xap4o.kwony.http

import com.xap4o.kwony.utils.Try
import com.xap4o.kwony.utils.await
import com.xap4o.kwony.utils.basicAuthHeader
import com.xap4o.kwony.utils.oauth2Header
import com.xap4o.kwony.utils.toMultiMap
import com.xap4o.kwony.utils.toNormalMap
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.ext.web.codec.impl.BodyCodecImpl
import java.net.URL
import java.time.Duration
import java.util.concurrent.CompletableFuture
import io.vertx.ext.web.client.HttpResponse as VxHttpResponse


data class HttpRequest(
        val url: URL,
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

data class HttpResponse(
        val statusCode: Int,
        val headers: Map<String, String> = emptyMap(),
        val body: Buffer
)

interface HttpClient {

//    suspend fun executeRaw(req: HttpRequest): Try<VxHttpResponse<Buffer>>

    suspend fun execute(req: HttpRequest): Try<HttpResponse>

    suspend fun expect(req: HttpRequest): Try<HttpResponse> = execute(req).filter { it.statusCode in 200..299 }
}

class HttpClientImpl(vertx: Vertx) : HttpClient {
    val webClient = WebClient.create(vertx)
    val httpsWebClient = WebClient.create(vertx, WebClientOptions().setSsl(true))

    private suspend fun executeRaw(req: HttpRequest): Try<VxHttpResponse<Buffer>> {
        val future = CompletableFuture<VxHttpResponse<Buffer>>()
        fun handler(res: AsyncResult<VxHttpResponse<Buffer>>) {
            if (res.succeeded())
                future.complete(res.result())
            else
                future.completeExceptionally(res.cause())
        }

        val port = if (req.url.port == -1) req.url.defaultPort else req.url.port
        val client = if (req.url.protocol == "https") httpsWebClient else webClient
        val vertxReq = client.request(req.method, port, req.url.host, req.url.path)
//        vertxReq.ssl(url.protocol == "https")
        vertxReq.timeout(req.timeout.toMillis())
        req.params.forEach { (name, value) -> vertxReq.addQueryParam(name, value) }
        req.headers.forEach { (name, value) -> vertxReq.putHeader(name, value) }
        when (req.body) {
            is Empty -> vertxReq.send(::handler)
            is Json -> vertxReq.sendJson(req.body.payload, ::handler)
            is Form -> vertxReq.sendForm(req.body.params.toMultiMap(), ::handler)
        }
        return future.await()
    }

    suspend override fun execute(req: HttpRequest): Try<HttpResponse> = executeRaw(req).map(::convertResponse)
}


inline suspend fun <reified T> HttpClient.json(req: HttpRequest): Try<T> =
        expect(req).map { BodyCodecImpl.jsonDecoder<T>(T::class.java).apply(it.body) }

fun convertResponse(vxResponse: VxHttpResponse<Buffer>): HttpResponse =
        HttpResponse(vxResponse.statusCode(), vxResponse.headers().toNormalMap(), vxResponse.body())




