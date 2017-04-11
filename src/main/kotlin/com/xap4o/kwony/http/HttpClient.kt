package com.xap4o.kwony.http

import io.netty.handler.codec.http.HttpRequest
import java.time.Duration
import java.util.concurrent.Future


interface HttpClient {
    fun <T> make(req: HttpRequest, timeout: Duration): Future<T>

}
class HttpClientImpl : HttpClient {
    override fun <T> make(req: HttpRequest, timeout: Duration): Future<T> {
        TODO()
    }
}


