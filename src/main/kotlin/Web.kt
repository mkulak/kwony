import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Router

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val server = vertx.createHttpServer()
    val router = Router.router(vertx).apply {
        route(HttpMethod.GET, "/list").handler { context ->
            context.response().endWithJson(listOf(1, 2, 3))
        }
        route(HttpMethod.GET, "/*").handler { context ->
            val request = context.request()
            val foo = request.getParam("foo")
            val response = context.response()
//        response.putHeader("content-type", "text/plain")
            response.endWithJson("Hello World foo=$foo")
        }
    }

    server.requestHandler(router::accept).listen(8080)
}

fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
}