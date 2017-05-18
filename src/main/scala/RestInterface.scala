import SolarTimeFrameProtocol.{Question, SolarTimeFrame, SolarTimeFrameRequested}
import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.util.Timeout
import spray.http.StatusCodes
import spray.routing.{HttpService, HttpServiceActor, RequestContext, Route}

import scala.concurrent.duration._

class RestInterface extends HttpServiceActor with RestApi {

  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging {
  actor: Actor =>

  implicit val timeout = Timeout(10 seconds)

  var solarTimeFrames: Vector[Question] = Vector[Question]()

  def routes: Route = {
    import SolarTimeFrameProtocol._
    import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller

    pathPrefix("solartimeframe") {
      pathEnd {
        post {
          entity(as[Question]) { stf =>
            requestContext =>
              val responder = createResponder(requestContext)
              solarTimeFrames = solarTimeFrames :+ stf
              responder ! SolarTimeFrameRequested
          }
        }
      }
    }
  }

  private def createResponder(requestContext: RequestContext) = {
    context.actorOf(Props(new Responder(requestContext)))
  }
}


class Responder(requestContext: RequestContext) extends Actor with ActorLogging {

  def receive = {

    case SolarTimeFrameRequested =>
      requestContext.complete(StatusCodes.Created)
      killYourself
  }

  private def killYourself = self ! PoisonPill

}