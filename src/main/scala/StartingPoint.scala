import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import org.slf4j.LoggerFactory
import spray.can.Http

import scala.concurrent.duration._

object StartingPoint extends App {

  val config = ConfigFactory.load().withValue("http.host", ConfigValueFactory.fromAnyRef("localhost")).withValue("http.port", ConfigValueFactory.fromAnyRef("8080"))
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  val logger = LoggerFactory.getLogger(this.getClass)

  implicit val system = ActorSystem("solar-measurement-system")

  val api = system.actorOf(Props(new RestInterface()), "httpInterface")

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

  IO(Http).ask(Http.Bind(listener = api, interface = host, port = port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        println(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        println("REST interface could not bind to " +
          s"$host:$port, ${cmd.failureMessage}")
        system.terminate
    }

}
