package com.xap4o.kwony.twitter

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.http.Form
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import com.xap4o.kwony.utils.Logging
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod


interface TwitterClient {
  fun open(): Future<Token>
  fun search(token: Token, keyword: String): Future<SearchResponse>
}

class TwitterClientImpl(val config: ProcessingConfig, val http: HttpClient) : TwitterClient, Logging {

  override fun open(): Future<Token> {
      val req = HttpRequest("${config.twitterHost}/oauth2/token", HttpMethod.POST)
              .withBody(Form(mapOf("grant_type" to "client_credentials")))
              .withTimeout(config.timeout)
              .withBasicAuth(config.twitterKey, config.twitterSecret)
      return http.make(req, AuthResponse::class.java).map { Token(it.accessToken) }
  }

  override fun search(token: Token, keyword: String): Future<SearchResponse> {
      val req = HttpRequest("${config.twitterHost}/1.1/search/tweets.json")
              .withParams(mapOf("q" to keyword))
              .withTimeout(config.timeout)
              .withOAuth2(token.value)
      return http.make(req, SearchResponse::class.java)
  }
}

data class Token(val value: String)

