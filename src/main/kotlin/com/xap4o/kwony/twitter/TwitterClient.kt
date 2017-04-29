package com.xap4o.kwony.twitter

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.http.Form
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Try
import com.xap4o.kwony.utils.await
import com.xap4o.kwony.utils.json
import com.xap4o.kwony.utils.map
import io.vertx.core.http.HttpMethod


interface TwitterClient {
    suspend fun open(): Try<Token>
    suspend fun search(token: Token, keyword: String): Try<SearchResponse>
}

class TwitterClientImpl(val config: ProcessingConfig, val http: HttpClient) : TwitterClient, Logging {

    override suspend fun open(): Try<Token> { //TODO MK: cache token
        val req = HttpRequest("${config.twitterHost}/oauth2/token", HttpMethod.POST)
                .withBody(Form(mapOf("grant_type" to "client_credentials")))
                .withTimeout(config.timeout)
                .withBasicAuth(config.twitterKey, config.twitterSecret)
                .withHeader("Content-Type" to "x-www-form-urlencoded; charset=utf-8")
        return http.json<AuthResponse>(req).map { Token(it.accessToken) }.await()
    }

    override suspend fun search(token: Token, keyword: String): Try<SearchResponse> {
        val req = HttpRequest("${config.twitterHost}/1.1/search/tweets.json")
                .withParams(mapOf("q" to keyword))
                .withTimeout(config.timeout)
                .withOAuth2(token.value)
        return http.json<SearchResponse>(req).await()
    }
}

data class Token(val value: String)

