package com.sclasen.sprack

import akka.actor.Actor
import spray.util.SprayActorLogging
import akka.util.Timeout
import spray.can.Http
import spray.http.{ChunkedMessageEnd, ChunkedResponseStart, HttpRequest}
import concurrent.duration._
import scala.concurrent.future

case object Ready

class SprackService(config: String, host:String, port:Int) extends Actor with SprayActorLogging {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'

  import concurrent.ExecutionContext.Implicits.global

  val rackApp = new RackApp(config, host, port)

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
      }
    }
    case Ready => sender ! Ready
    case huh:AnyRef => println(huh)
  }


}
