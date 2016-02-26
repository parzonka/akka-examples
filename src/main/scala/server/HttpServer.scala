package server

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.stream._
import akka.stream.scaladsl.Flow
import com.typesafe.config.{ ConfigFactory, Config }
import scala.concurrent.Future
import akka.stream.scaladsl.Sink

object HttpServer extends App {
  implicit val system = ActorSystem("akka-http-sample")
  implicit val materializer = ActorMaterializer()
  val serverSource = Http().bind(interface = "localhost", port = 8080)
  
  val requestHandler: HttpRequest => HttpResponse = {
    
    case HttpRequest(GET, Uri.Path("/"), _, _, _) => 
      HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world!</body></html>"))

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) => HttpResponse(entity = "PONG!")

    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) => sys.error("BOOM!")

    case _: HttpRequest => HttpResponse(404, entity = "Unknown resource!")
  }
  
  val bindingFuture: Future[Http.ServerBinding] =
  serverSource.to(Sink.foreach { connection =>
    println("Accepted new connection from " + connection.remoteAddress)
 
    connection handleWithSyncHandler requestHandler

  }).run()

}
