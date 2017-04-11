package com.xap4o.kwony.twitter

import com.xap4o.kwony.config.ProcessingConfig
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.utils.Logging
import java.util.concurrent.Future


interface TwitterClient {
  fun open(): Future<Token>
  fun search(token: Token, keyword: String): Future<SearchResponse>
}

class TwitterClientImpl(config: ProcessingConfig, http: HttpClient) : TwitterClient, Logging {
//  val contentType = ContentType(MediaType.applicationWithFixedCharset("x-www-form-urlencoded", HttpCharsets.`UTF-8`))

  override fun open(): Future<Token> {
//    val req: HttpRequest = HttpRequest()
//      .withUri(s"${config.twitterHost}/oauth2/token")
//      .withMethod(HttpMethods.POST)
//      .withHeaders(Authorization(BasicHttpCredentials(config.twitterKey, config.twitterSecret)))
//      .withEntity(HttpEntity(contentType, "grant_type=client_credentials"))
//
//    http.make<AuthResponse>(req, config.timeout).rightMap(r => Token(r.accessToken))
      TODO()
  }

  override fun search(token: Token, keyword: String): Future<SearchResponse> {
//    val req: HttpRequest = HttpRequest()
//      .withUri(Uri("${config.twitterHost}/1.1/search/tweets.json").withQuery(Query("q" -> keyword)))
//      .withMethod(HttpMethods.GET)
//      .withHeaders(Authorization(OAuth2BearerToken(token.value)))
//
//    return http.make<SearchResponse>(req, config.timeout)
      TODO()
  }
}

data class Token(val value: String)

