package com.sclasen.sprack

import akka.actor.Actor
import spray.util.SprayActorLogging
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpRequest
import concurrent.duration._
import scala.concurrent.future

class SprackService(config: String = "config.ru") extends Actor with SprayActorLogging {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'

  import concurrent.ExecutionContext.Implicits.global

  val rackApp = new RackApp(config)

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case r: HttpRequest => {
      val client = sender
      future {
        client ! rackApp.call(r).toSpray
      }
    }
    case huh:AnyRef => println(huh)
  }


}
