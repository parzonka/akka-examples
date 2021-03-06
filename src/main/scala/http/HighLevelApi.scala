package http
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
 
/**
 * Uses the high level http and routing API
 */
object HighLevelApi extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
 
  val route =
    path("hello") {
      get {
        complete {
          "Say hello to akka-http"
        }
      }
    }
 
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
 
  // listen for line
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  scala.io.StdIn.readLine()
  
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}