import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers

object HttpServer {

  def bindTo(args: Array[String]) = if (args(0) == null ) "localhost" else args(0)

  def main(args: Array[String]) {
    val server = Undertow.builder().addHttpListener(8080, bindTo(args)).setHandler(new HttpHandler() {
      override def handleRequest( exchange: HttpServerExchange) = {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain")
        exchange.getResponseSender().send("JBoss Weekly Editorial App is Up")
      }
    }).build()
    server.start()
  }
}
