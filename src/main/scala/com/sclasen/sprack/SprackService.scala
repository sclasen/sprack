package com.sclasen.sprack

import akka.actor.Actor
import spray.util.SprayActorLogging
import akka.util.Timeout
import spray.can.Http
import spray.http._
import concurrent.duration._
import scala.concurrent.future
import spray.http.HttpRequest
import spray.http.ChunkedMessageEnd
import spray.http.HttpResponse
import spray.http.ChunkedResponseStart
import akka.dispatch.Dispatchers

case object Ready

class SprackService(config: String, port:Int) extends Actor with SprayActorLogging {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'

  implicit val futureDispatcher = context.system.dispatchers.lookup("sprack.rack-dispatcher")

  val rackApp = new RackApp(config, port)

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case r: HttpRequest => {
      val client = sender
      future {
        rackApp.call(r) match {
          case Right(response) => client ! response
          case Left((response, chunks)) =>
            client ! ChunkedResponseStart(response)
            chunks.foreach(ch => client ! ch)
            client ! ChunkedMessageEnd()
        }
      }.onFailure{
        case e:Exception =>
          println("=======================>")
          e.printStackTrace()
          client ! HttpResponse(StatusCodes.InternalServerError, HttpEntity(e.toString + e.getStackTraceString))
      }
    }
    case Ready => sender ! Ready
    case huh:AnyRef => println(huh)
  }


}
