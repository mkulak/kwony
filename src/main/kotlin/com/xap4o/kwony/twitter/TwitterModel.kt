package com.xap4o.kwony.twitter


data class AuthResponse(val tokenType: String, val accessToken: String)

data class SearchResponse(val tweets: List<Tweet>, val metadata: SearchMetadata)

data class SearchMetadata(val count: Int, val query: String)

data class Tweet(val text: String, val username: String)

//object TwitterModel {


//  implicit object AuthResponseJsonFormat extends RootJsonFormat[AuthResponse] {
//    def write(r: AuthResponse): JsValue =
//      JsObject("token_type" -> JsString(r.tokenType), "access_token" -> JsString(r.accessToken))
//
//    def read(value: JsValue): AuthResponse = value match {
//      case JsObject(values) =>
//        AuthResponse(
//          values("token_type").asInstanceOf[JsString].value,
//          values("access_token").asInstanceOf[JsString].value
//        )
//      case _ => deserializationError(s"bad data $value")
//    }
//  }
//  implicit object TweetJsonFormat extends RootJsonFormat[Tweet] {
//    def write(t: Tweet): JsValue =
//      JsObject("text" -> JsString(t.text), "user" -> JsObject("name" -> JsString(t.username)))
//
//    def read(value: JsValue): Tweet = value match {
//      case JsObject(values) =>
//        Tweet(
//          values("text").asInstanceOf[JsString].value,
//          values("user").asJsObject.fields("name").asInstanceOf[JsString].value
//        )
//      case _ => deserializationError(s"bad data $value")
//    }
//  }

//  implicit object SearchMetadataJsonFormat extends RootJsonFormat[SearchMetadata] {
//    def write(t: SearchMetadata): JsValue =
//      JsObject("count" -> JsNumber(t.count), "query" -> JsString(t.query))
//
//    def read(value: JsValue): SearchMetadata = value match {
//      case JsObject(values) =>
//        SearchMetadata(
//          values("count").asInstanceOf[JsNumber].value.intValue(),
//          values("query").asInstanceOf[JsString].value
//        )
//      case _ => deserializationError(s"bad data $value")
//    }
//  }
//
//
//  implicit object SearchResponseJsonFormat extends RootJsonFormat[SearchResponse] {
//    def write(t: SearchResponse): JsValue =
//      JsObject("statuses" -> JsArray(t.tweets.map(_.toJson).toVector), "metadata" -> t.metadata.toJson)
//
//    def read(value: JsValue): SearchResponse = value match {
//      case JsObject(values) =>
//        SearchResponse(
//          values("statuses").asInstanceOf[JsArray].elements.map(_.convertTo[Tweet]),
//          values("search_metadata").convertTo[SearchMetadata]
//        )
//      case _ => deserializationError(s"bad data $value")
//    }
//  }
//
//
//  implicit class EncodableString(val s: String) extends AnyVal {
//    def urlEncode(): String = URLEncoder.encode(s, "UTF-8")
//    def base64Encode(): String = new String(Base64.getEncoder.encode(s.getBytes("UTF-8")), "UTF-8")
//  }
//}

