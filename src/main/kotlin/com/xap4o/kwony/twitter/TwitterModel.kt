package com.xap4o.kwony.twitter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class TwitterToken(val value: String)

data class AuthResponse(
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("access_token") val accessToken: String
)

data class SearchResponse(
    @JsonProperty("statuses") val tweets: List<Tweet>,
    @JsonProperty("search_metadata") val metadata: SearchMetadata
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchMetadata(val count: Int, val query: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Tweet(val text: String, val user: User)

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(val id: Long, val name: String)

data class Keyword(val value: String)

