package com.xap4o.kwony.twitter

import com.xap4o.kwony.config.TwitterConfig
import com.xap4o.kwony.http.Form
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import com.xap4o.kwony.http.json
import com.xap4o.kwony.utils.Logging
import com.xap4o.kwony.utils.Try
import com.xap4o.kwony.utils.withPath
import io.vertx.core.http.HttpMethod


interface TwitterClient {
    suspend fun open(): Try<TwitterToken>
    suspend fun search(token: TwitterToken, keyword: Keyword): Try<SearchResponse>
}

class TwitterClientImpl(val config: TwitterConfig, val http: HttpClient) : TwitterClient, Logging {

    override suspend fun open(): Try<TwitterToken> = //TODO MK: cache token
            Try {
                val req = HttpRequest(config.host.withPath("/oauth2/token"), HttpMethod.POST)
                        .withBody(Form(mapOf("grant_type" to "client_credentials")))
                        .withTimeout(config.timeout)
                        .withBasicAuth(config.key, config.secret)
                        .withHeader("Content-Type" to "x-www-form-urlencoded; charset=utf-8")
                val result = http.json<AuthResponse>(req).withErrorMessage("Failed to get twitter token").orDie()
                TwitterToken(result.accessToken)
            }

    override suspend fun search(token: TwitterToken, keyword: Keyword): Try<SearchResponse> {
        val req = HttpRequest(config.host.withPath("/1.1/search/tweets.json"))
                .withParams(mapOf("q" to keyword.value))
                .withTimeout(config.timeout)
                .withOAuth2(token.value)
        return http.json<SearchResponse>(req).withErrorMessage("Failed to search twitter for '$keyword'")
    }
}


