package com.xap4o.kwony.twitter

import com.xap4o.kwony.config.TwitterConfig
import com.xap4o.kwony.http.*
import com.xap4o.kwony.utils.*
import io.vertx.core.http.HttpMethod


interface TwitterClient {
    fun open(): Future<TwitterToken>
    fun search(token: TwitterToken, keyword: Keyword): Future<SearchResponse>
}

class TwitterClientImpl(val config: TwitterConfig, val http: HttpClient) : TwitterClient, Logging {

    override fun open(): Future<TwitterToken> { //TODO MK: cache token
        val req = HttpRequest(config.host.withPath("/oauth2/token"), HttpMethod.POST)
                .withBody(Form(mapOf("grant_type" to "client_credentials")))
                .withTimeout(config.timeout)
                .withBasicAuth(config.key, config.secret)
                .addHeader("Content-Type" to "x-www-form-urlencoded; charset=utf-8")
        return http.json<AuthResponse>(req).map { TwitterToken(it.accessToken) }
                .withErrorMessage("Failed to get twitter token")
    }

    override fun search(token: TwitterToken, keyword: Keyword): Future<SearchResponse> {
        val req = HttpRequest(config.host.withPath("/1.1/search/tweets.json"))
                .addParam("q" to keyword.value)
                .withTimeout(config.timeout)
                .withOAuth2(token.value)
        return http.json<SearchResponse>(req).withErrorMessage("Failed to search twitter for '$keyword'")
    }
}


