package com.xap4o.kwony.twitter

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.xap4o.kwony.config.TwitterConfig
import com.xap4o.kwony.http.Form
import com.xap4o.kwony.http.HttpClient
import com.xap4o.kwony.http.HttpRequest
import com.xap4o.kwony.http.HttpResponse
import com.xap4o.kwony.utils.Success
import com.xap4o.kwony.utils.Try
import io.vertx.core.buffer.impl.BufferFactoryImpl
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL
import java.time.Duration

class TwitterClientImplTest {
    init {
        Json.mapper.registerKotlinModule()
    }

    val config = TwitterConfig(URL("https://localhost:8080"), "key", "secret", Duration.ofSeconds(10))

    @Test fun `open should return valid twitter token`() {
        runBlocking {
            val mockHttpClient = TestHttpClient { req ->
                assertThat(req.method).isEqualTo(HttpMethod.POST)
                assertThat(req.url.toString()).isEqualTo("${config.host}/oauth2/token")
                assertThat(req.body).isEqualTo(Form(mapOf("grant_type" to "client_credentials")))
                assertTrue(req.headers.containsKey("Authorization"))
                JsonObject(mapOf("access_token" to "123", "token_type" to "t")).toResponse()
            }
            val twitterClient = TwitterClientImpl(config, mockHttpClient)
            val token = twitterClient.open()
            assertThat(token).isEqualTo(Success(TwitterToken("123")))
        }
    }

    @Test fun `search should find tweets`() {
        runBlocking {
            val token = "123"
            val keyword = "cheese"
            val mockHttpClient = TestHttpClient { req ->
                assertThat(req.method).isEqualTo(HttpMethod.GET)
                assertThat(req.url.toString()).isEqualTo("${config.host}/1.1/search/tweets.json")
                assertThat(req.params["q"]).isEqualTo(keyword)
                assertThat(req.headers["Authorization"]).endsWith(token)
                JsonObject(mapOf("statuses" to JsonArray(), "search_metadata" to JsonObject(mapOf("count" to 0, "query" to keyword)))).toResponse()
            }
            val twitterClient = TwitterClientImpl(config, mockHttpClient)

            val result = twitterClient.search(TwitterToken(token), Keyword(keyword))
            assertThat(result).isEqualTo(Success(SearchResponse(emptyList(), SearchMetadata(0, keyword))))
        }
    }

}


val bufferFactory = BufferFactoryImpl()

fun JsonObject.toResponse(): Try<HttpResponse> {
    return Success(HttpResponse(200, body = bufferFactory.buffer(Json.encodePrettily(this))))
}

class TestHttpClient(val f: (HttpRequest) -> Try<HttpResponse>) : HttpClient {
    suspend override fun execute(req: HttpRequest): Try<HttpResponse> = f(req)
}
