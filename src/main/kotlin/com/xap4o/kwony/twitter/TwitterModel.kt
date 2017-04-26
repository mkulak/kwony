package com.xap4o.kwony.twitter

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthResponse(
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("access_token") val accessToken: String
)

data class SearchResponse(
    @JsonProperty("statuses") val tweets: List<Tweet>,
    @JsonProperty("search_metadata") val metadata: SearchMetadata
)

data class SearchMetadata(val count: Int, val query: String)

data class Tweet(val text: String, @JsonProperty("user.name") val username: String)

