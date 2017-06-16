package com.xap4o.kwony.http

import com.xap4o.kwony.utils.Try
import com.xap4o.kwony.utils.await
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.ext.web.codec.impl.BodyCodecImpl
import java.util.concurrent.CompletableFuture
import io.vertx.ext.web.client.HttpResponse as VxHttpResponse


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
        val port = if (req.url.port == -1) req.url.defaultPort else req.url.port
        val client = if (req.url.protocol == "https") httpsWebClient else webClient
        val vertxReq = client.request(req.method, port, req.url.host, req.url.path)
//        vertxReq.ssl(url.protocol == "https")
        vertxReq.timeout(req.timeout.toMillis())
        req.params.forEach { (name, value) -> vertxReq.addQueryParam(name, value) }
        req.headers.forEach { (name, value) -> vertxReq.putHeader(name, value) }
        vertxReq.sendBuffer(req.body) {
            if (it.succeeded())
                future.complete(it.result())
            else
                future.completeExceptionally(it.cause())
        }
        return future.await()
    }

    suspend override fun execute(req: HttpRequest): Try<HttpResponse> = executeRaw(req).map(::convertResponse)
}


inline suspend fun <reified T> HttpClient.json(req: HttpRequest): Try<T> =
        expect(req).map { BodyCodecImpl.jsonDecoder<T>(T::class.java).apply(it.body) }

fun convertResponse(vxResponse: VxHttpResponse<Buffer>): HttpResponse =
        HttpResponse(vxResponse.statusCode(), vxResponse.headers().toNormalMap(), vxResponse.body())




