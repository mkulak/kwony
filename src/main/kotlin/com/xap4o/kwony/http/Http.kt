package com.xap4o.kwony.http

import io.netty.buffer.UnpooledByteBufAllocator
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder
import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import java.net.URL
import java.time.Duration
import java.util.Base64


data class HttpRequest(
        val url: URL,
        val method: HttpMethod = HttpMethod.GET,
        val params: Map<String, String> = emptyMap(),
        val body: Buffer = Buffer.buffer(),
        val headers: Map<String, String> = emptyMap(),
        val timeout: Duration = Duration.ofSeconds(10)) {

    fun withMethod(method: HttpMethod) = copy(method = method)

    fun withBody(body: ResponseBody) = copy(body = body.toBuffer())

    fun withParams(params: Map<String, String>) = copy(params = params)

    fun withHeader(header: Pair<String, String>) = copy(headers = headers + header)

    fun withBasicAuth(name: String, password: String) = withHeader(basicAuthHeader(name, password))

    fun withTimeout(timeout: Duration) = copy(timeout = timeout)

    fun withOAuth2(token: String) = withHeader(oauth2Header(token))
}

sealed class ResponseBody

data class FormBody(val params: Map<String, String>) : ResponseBody()

data class JsonBody(val payload: Any) : ResponseBody()

data class BinaryBody(val data: Buffer) : ResponseBody()

object EmptyBody : ResponseBody() {

    override fun toString() = "EmptyBody"
}

data class HttpResponse(
        val statusCode: Int,
        val headers: Map<String, String> = emptyMap(),
        val body: Buffer
)


typealias Handler = (HttpRequest) -> HttpResponse

data class Route(val method: HttpMethod, val path: String, val handler: Handler)

fun HttpServer.start(router: Router, url: URL) = requestHandler(router::accept).listen(url.port, url.host)

fun Map<String, String>.toMultiMap(): MultiMap = MultiMap.caseInsensitiveMultiMap().apply { addAll(this@toMultiMap) }

fun MultiMap.toNormalMap(): Map<String, String> = asIterable().map { (k, v) -> k to v }.toMap()

fun String.encodeBase64(): String =
        Base64.getEncoder().encode(this.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)

fun basicAuthHeader(name: String, password: String): Pair<String, String> =
        "Authorization" to ("Basic " + ("$name:$password".encodeBase64()))

fun oauth2Header(token: String): Pair<String, String> =
        "Authorization" to "Bearer $token"

fun URL.withPath(path: String): URL = URL("$this$path")


operator fun Router.plus(other: Router): Router = mountSubRouter("/", other)

fun jsonResponse(body: Any): HttpResponse =
        HttpResponse(200, mapOf("Content-Type" to "application/json; charset=utf-8"), Buffer.buffer(Json.encodePrettily(body)))


fun ResponseBody.toBuffer(): Buffer =
    when(this) {
//Content type?
        is EmptyBody -> Buffer.buffer()
        is BinaryBody -> data
        is JsonBody -> Buffer.buffer(Json.encode(payload))
        is FormBody -> {
            val attributes = params
            val multipart = "multipart/form-data" == contentType
            val request = DefaultFullHttpRequest(HttpVersion.HTTP_1_1, io.netty.handler.codec.http.HttpMethod.POST, "/")
            val encoder = HttpPostRequestEncoder(request, multipart)
            for ((key, value) in attributes) {
                encoder.addBodyAttribute(key, value)
            }
            encoder.finalizeRequest()
            for (headerName in request.headers().names()) {
                req.putHeader(headerName, request.headers().get(headerName))
            }
            if (encoder.isChunked) {
                val buffer = Buffer.buffer()
                while (true) {
                    val chunk = encoder.readChunk(UnpooledByteBufAllocator(false))
                    val content = chunk.content()
                    if (content.readableBytes() == 0) {
                        break
                    }
                    buffer.appendBuffer(Buffer.buffer(content))
                }
                buffer
            } else {
                Buffer.buffer(request.content())
            }
        }
    }


fun Buffer.toJsonObj(): JsonObject = JsonObject(toString())

